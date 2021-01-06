package com.maxleap.mysqlproxy.async.pool;

 

import static io.vertx.mysqlclient.impl.protocol.Packets.ERROR_PACKET_HEADER;
import static io.vertx.mysqlclient.impl.protocol.Packets.OK_PACKET_HEADER;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.mysqlclient.MySQLException;
import io.vertx.mysqlclient.impl.util.BufferUtils;

public class MysqlHandler extends ChannelInboundHandlerAdapter {
	public static final int OK_PACKET_HEADER = 0x00;
	public static final int EOF_PACKET_HEADER = 0xFE;
	public static final int ERROR_PACKET_HEADER = 0xFF;
	public static final int PACKET_PAYLOAD_LENGTH_LIMIT = 0xFFFFFF;
	private final ChannelPromise handshakeCompletedPromise;
	private final ChannelPromise connectionInitialized;
	
	private boolean connected =false;
	private NetSocket serverSocket;

	public void setServerSocket(NetSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public MysqlHandler(ChannelPromise handshakeCompletedPromise, ChannelPromise connectionInitialized) {
		this.handshakeCompletedPromise = handshakeCompletedPromise;
		this.connectionInitialized = connectionInitialized;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		ByteBuf payload = (ByteBuf) msg;
//		System.out.println(ByteBufUtil.prettyHexDump(payload));
		handle(payload);

	}
	
    public void handle(ByteBuf in) {

//    	System.out.println("mysql-server:"+ByteBufUtil.prettyHexDump(in));
        if (in.readableBytes() > 4) {
        	if(!connected) {
        		 int packetStartIdx = in.readerIndex();
                 int payloadLength = in.readUnsignedMediumLE();
                 int sequenceId = in.readUnsignedByte();
                 // read a non-split packet
                 handleAuthentication(in.readSlice(payloadLength));
        	} else {
        		
        		if( serverSocket!=null ) {
        			
        			serverSocket.write(Buffer.buffer(in.duplicate()));
        		}
        	}
           
           
        }
    }
    
    private void handleAuthentication(ByteBuf payload) {
        int header = payload.getUnsignedByte(payload.readerIndex());
        switch (header) {
            case OK_PACKET_HEADER:
            	connected = true;
            	connectionInitialized.setSuccess();
    			break;
            case ERROR_PACKET_HEADER:
                handleErrorPacketPayload(payload);
                
                break;
//            case AUTH_SWITCH_REQUEST_STATUS_FLAG:
////                handleAuthSwitchRequest(cmd.password().getBytes(StandardCharsets.UTF_8), payload);
//                break;
//            case AUTH_MORE_DATA_STATUS_FLAG:
////                handleAuthMoreData(cmd.password().getBytes(StandardCharsets.UTF_8), payload);
//                break;
            default:
//                completionHandler.handle(CommandResponse.failure(new IllegalStateException("Unhandled state with header: " + header)));
            	connectionInitialized.setFailure(new IllegalStateException("Unhandled state with header: " + header));
        }
    }

	void handleErrorPacketPayload(ByteBuf payload) {
		payload.skipBytes(1); // skip ERR packet header
		int errorCode = payload.readUnsignedShortLE();
		// CLIENT_PROTOCOL_41 capability flag will always be set
		payload.skipBytes(1); // SQL state marker will always be #
		String sqlState = BufferUtils.readFixedLengthString(payload, 5, StandardCharsets.UTF_8);
		String errorMessage = readRestOfPacketString(payload, StandardCharsets.UTF_8);
		connectionInitialized.setFailure(new MySQLException(errorMessage, errorCode, sqlState));
	}

	String readRestOfPacketString(ByteBuf payload, Charset charset) {
		return BufferUtils.readFixedLengthString(payload, payload.readableBytes(), charset);
	}
	
	
}
