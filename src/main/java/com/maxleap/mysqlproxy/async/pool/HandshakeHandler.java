package com.maxleap.mysqlproxy.async.pool;


 
import java.nio.charset.Charset;

import com.maxleap.mysqlproxy.util.BuffUtil;
import com.maxleap.mysqlproxy.util.Native41Authenticator;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mysqlclient.impl.protocol.CapabilitiesFlag;


public class HandshakeHandler extends ChannelInboundHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(HandshakeHandler.class);
	
	private int maxThreeBytes = 4*255 * 255 * 255;
	
	private final ChannelPromise handshakeCompletedPromise;
	private final ChannelPromise connectionInitialized;
	
	
	public HandshakeHandler(ChannelPromise handshakeCompletedPromise, ChannelPromise connectionInitialized) {
		this.handshakeCompletedPromise = handshakeCompletedPromise;
		 this.connectionInitialized = connectionInitialized;
    }
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception { 
		
    	ByteBuf buf = (ByteBuf) msg;
    	System.out.println(ByteBufUtil.prettyHexDump(buf));
    	if( (buf.getByte(4) & 0xff) != 0xff ) { 
    		ctx.pipeline().remove(this);
    		ctx.pipeline().addLast("mysqlhandler",new MysqlHandler(handshakeCompletedPromise,connectionInitialized));
        	doHandShake(ctx, buf);
        	handshakeCompletedPromise.setSuccess();
        	ctx.fireChannelRead(msg);
    	} else {
    		ctx.close();
    		ReferenceCountUtil.release(msg);
    	}
    	
    	
    }



	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("meilianshang");
		super.channelInactive(ctx);
	}
    private void doHandShake(ChannelHandlerContext ctx,ByteBuf buf)throws Exception { 
//		System.out.println(ByteBufUtil.prettyHexDump(buf));

    	System.out.println("handshake");
    	int head = buf.readIntLE();
    	int length = head & 0x00FFFFFF;
    	int seq = length & 0xFF000000;
    	int protocolVersion = buf.readByte();
    	if(protocolVersion==-1) {
    		// TODO 错误处理
    	}
    	String version  = BuffUtil.readString(buf);
    	int thread = buf.readIntLE();
    	String seed= null;
    	if (protocolVersion > 9) {
            // read auth-plugin-data-part-1 (string[8])
            seed = BuffUtil.readString(buf,8);
            // read filler ([00])
            buf.readByte();
        } else {
            // read scramble (string[NUL])
            seed = BuffUtil.readString(buf);
        } 
    	int serverCapabilities = buf.readShortLE();
    	if( ((protocolVersion > 9) && (serverCapabilities & CapabilitiesFlag.CLIENT_PROTOCOL_41) != 0) ) {
    		int serverCharsetIndex = buf.readByte();
    		int serverStatus = buf.readShortLE();
    		serverCapabilities  |= buf.readShortLE() << 16;
    		
    	}
    	int authPluginDataLength=0;
    	 if ((serverCapabilities &  0x00080000) != 0) {
             // read length of auth-plugin-data (1 byte)
              authPluginDataLength = buf.readByte() & 0xff;
         } else {
             // read filler ([00])
             buf.readByte();
         }
    	 buf.skipBytes(10);
    	 if ((serverCapabilities & CapabilitiesFlag.CLIENT_SECURE_CONNECTION) != 0) {
    		 String seedPart2;
             StringBuilder newSeed;
             if (authPluginDataLength > 0) {
            	 ByteBuf seedPartBuf2 = buf.readBytes(authPluginDataLength - 8);
            	 seedPart2 = BuffUtil.readString(seedPartBuf2);
            	 seedPartBuf2.release();
                 newSeed = new StringBuilder( authPluginDataLength);
             } else {
            	 seedPart2 = BuffUtil.readString(buf);
                 newSeed = new StringBuilder(20);
             }
             newSeed.append(seed);
             newSeed.append(seedPart2);
             seed = newSeed.toString();
    	 }
    	 String authPluginName = BuffUtil.readString(buf);

    	 ByteBuf outMsg =  ctx.alloc().buffer();
    	 long clientParam=0;
//		  if (((serverCapabilities & MysqlFlags.CLIENT_COMPRESS) != 0) ) {
//	          clientParam |= MysqlFlags.CLIENT_COMPRESS;
//	      }
		  String database = "es";
		  if((database != null) && (database.length() > 0)){
			  clientParam |= CapabilitiesFlag.CLIENT_CONNECT_WITH_DB;
		  }
		  
		  if ((serverCapabilities & CapabilitiesFlag.CLIENT_LONG_FLAG) != 0) {
	            // We understand other column flags, as well
	            clientParam |= CapabilitiesFlag.CLIENT_LONG_FLAG;
	      }

	        // return FOUND rows
        clientParam |= CapabilitiesFlag.CLIENT_FOUND_ROWS;

        clientParam |= CapabilitiesFlag.CLIENT_LOCAL_FILES;
		  clientParam |= CapabilitiesFlag.CLIENT_PLUGIN_AUTH | CapabilitiesFlag.CLIENT_LONG_PASSWORD | CapabilitiesFlag.CLIENT_PROTOCOL_41 | CapabilitiesFlag.CLIENT_TRANSACTIONS // Need this to get server status values
                  | CapabilitiesFlag.CLIENT_MULTI_RESULTS // We always allow multiple result sets
                  | CapabilitiesFlag.CLIENT_SECURE_CONNECTION;
//		  if ((serverCapabilities & MysqlFlags.CLIENT_PLUGIN_AUTH) != 0) {
//			  if (((serverCapabilities & MysqlFlags.CLIENT_CAN_HANDLE_EXPIRED_PASSWORD) != 0) ) {
//                  clientParam |= MysqlFlags.CLIENT_CAN_HANDLE_EXPIRED_PASSWORD;
//              }
//              if (((serverCapabilities & MysqlFlags.CLIENT_CONNECT_ATTRS) != 0)  ) {
//                  clientParam |= MysqlFlags.CLIENT_CONNECT_ATTRS;
//              }
//              if ((serverCapabilities & MysqlFlags.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA) != 0) {
//                  clientParam |= MysqlFlags.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA;
//              }  
//		  }
    	  //password
		// TODO
    	 byte[] auth = Native41Authenticator.encode("123456", Charset.forName("utf-8"),seed.getBytes());
//    	 outMsg.writeIntLE((int)696975);
    	 outMsg.writeIntLE((int)clientParam);
    	 outMsg.writeIntLE(maxThreeBytes);
    	 outMsg.writeByte(33);//utf-8
    	 outMsg.writeBytes(new byte[23]);
    	 // username
    	 outMsg.writeBytes("root".getBytes());
    	 outMsg.writeByte(0);
    	 
    	 outMsg.writeByte(auth.length);
    	 outMsg.writeBytes(auth);
    	 // db
    	 outMsg.writeBytes(database.getBytes());
    	 outMsg.writeByte(0);
         
    	 if ((serverCapabilities & CapabilitiesFlag.CLIENT_PLUGIN_AUTH) != 0 &&authPluginName!=null) {
    		 outMsg.writeBytes( authPluginName.getBytes());
    		 outMsg.writeByte(0);
         }

         // connection attributes
    	 //TODO 
         if (((clientParam & CapabilitiesFlag.CLIENT_CONNECT_ATTRS) != 0)) {
//             sendConnectionAttributes(last_sent, enc, this.connection);
         }
         ByteBuf outPack =  ctx.alloc().buffer();
         outPack.writeMediumLE(outMsg.readableBytes());
         outPack.writeByte(seq+1);
         outPack.writeBytes(outMsg);
        
//         System.out.println(ByteBufUtil.prettyHexDump(outPack));
    	 ctx.writeAndFlush(outPack);
    	 outMsg.release();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    		throws Exception {
    	super.exceptionCaught(ctx, cause);
    }
	

    
}