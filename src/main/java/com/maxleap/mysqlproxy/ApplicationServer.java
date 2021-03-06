package com.maxleap.mysqlproxy;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletionStage;

import com.maxleap.mysqlproxy.async.pool.ChannelConnector;
import com.maxleap.mysqlproxy.async.pool.ChannelConnectorImpl;
import com.maxleap.mysqlproxy.async.pool.MysqlHandler;
import com.maxleap.mysqlproxy.async.pool.NettyChannelPool;
import com.maxleap.mysqlproxy.async.pool.NettyChannelPoolHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.impl.NetSocketImpl;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.mysqlclient.impl.protocol.CapabilitiesFlag;
import io.vertx.mysqlclient.impl.protocol.CommandType;
import io.vertx.mysqlclient.impl.util.BufferUtils;
import io.vertx.mysqlclient.impl.util.Native41Authenticator;

public class ApplicationServer {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationServer.class);

    public static void main(String[] args) {
        Vertx.vertx(new VertxOptions().setMetricsOptions(
                new DropwizardMetricsOptions().setEnabled(true).setJmxEnabled(true)
        )
//        		.setEventLoopPoolSize(1).setWorkerPoolSize(2)
        		).deployVerticle(new MysqlProxyServerVerticle());

    }

    public static class MysqlProxyServerVerticle extends AbstractVerticle {
        private final int port = 3306;
        private final String mysqlHost = "localhost";
        private  Bootstrap newBootstrap( EventLoopGroup eventLoopGroup )
	    {
	        Bootstrap bootstrap = new Bootstrap();
	        bootstrap.group( eventLoopGroup );
	        bootstrap.channel(  NioSocketChannel.class );
	        bootstrap.option( ChannelOption.SO_KEEPALIVE, true );
	        bootstrap.option( ChannelOption.SO_REUSEADDR, true );
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
	        return bootstrap;
	    }
        @Override
        public void start() throws Exception {
            NetServer netServer = vertx.createNetServer();//创建代理服务器
            InetSocketAddress address = new InetSocketAddress("172.17.11.58", 3306);
//            InetSocketAddress address = new InetSocketAddress("localhost", 3306);
    		ChannelConnector connector = new ChannelConnectorImpl();
    		Bootstrap bootstrap = newBootstrap(new NioEventLoopGroup() );
    		NettyChannelPoolHandler nettyChannelTracker = new NettyChannelPoolHandler(bootstrap.config().group().next());
    		NettyChannelPool pool =    new NettyChannelPool( address, connector, bootstrap, nettyChannelTracker, ChannelHealthChecker.ACTIVE, 10000,100 );
    		ByteBuf byte1= Unpooled.wrappedBuffer("#28000Access denied for user 'root'@'localhost' (using password: YES)".getBytes());
            netServer.connectHandler(
                    socket->{
                        int status =0;
//                        System.out.println("connected the server");
                        Buffer bytebuf =  Buffer.buffer();
                        //version
                        bytebuf.appendByte((byte) 10);
                        //服务器版本信息
                        bytebuf.appendBytes("5.5.56".getBytes()).appendByte((byte)0);
                        //线程id
                        bytebuf.appendIntLE((int)Thread.currentThread().getId());
                        // 验证随机数
                        byte[]  seed1 = RandomUtil.randomBytes(8);
                        bytebuf.appendBytes(seed1);
                        // 填充值
                        bytebuf.appendByte((byte)0);
                        int serverCapabilities = initCapabilitiesFlags();
                        // challenge high
                        bytebuf.appendShortLE( (short) serverCapabilities );
                        // charset utf8
                        bytebuf.appendByte( (byte) 0x21);
                        //server status
                        bytebuf.appendShortLE((byte) 0x0002);
                        // challenge high
                        bytebuf.appendByte((byte) (serverCapabilities>>16) );
                        bytebuf.appendByte((byte) (serverCapabilities>>24) );

                        // 挑战长度 chanllege 暂未使用
                        bytebuf.appendByte((byte)0x15);
                        byte[] emtpy = new byte[10];
                        Arrays.fill(emtpy, (byte)0);
                        bytebuf.appendBytes(emtpy);
                        byte[] seed2 = RandomUtil.randomBytes(12);
                        bytebuf.appendBytes(seed2);
                        bytebuf.appendByte((byte)0);
                        bytebuf.appendBytes("mysql_native_password".getBytes());
                        bytebuf.appendByte((byte)0);
                        byte[] seed = new byte[seed1.length+seed2.length];
                        System.arraycopy(seed1, 0, seed, 0, seed1.length);
                        System.arraycopy(seed2, 0, seed, seed1.length, seed2.length);
                        Buffer bytes = Buffer.buffer();
                        bytes.appendMediumLE(bytebuf.length()).appendByte((byte) 0);
                        bytes.appendBuffer(bytebuf);
//		System.out.println( ByteBufUtil.prettyHexDump(bytes));

                        socket.handler(new Handler<Buffer>() {
                            int status = 0;
                            @Override
                            public void handle(Buffer m) {
                                if(status==1) {
                                	CompletionStage<Channel> cs = pool.acquire();
                                	cs.handle( (channel,error)->{

                        				if( error!=null ) {
                        					error.printStackTrace();
                        					System.out.println("errorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerror。");
                        					socket.close();
                        					return null;
                        				}
//                        				System.out.println(channel.isActive());
                        				new MysqlProxyConnection(socket,channel,pool).proxy();
//                        				System.out.println(ByteBufUtil.prettyHexDump(m.getByteBuf()));
                        				channel.writeAndFlush(m.getByteBuf());
                        			
                        				return channel;
                        			} );
                                } else {
                                    ByteBuf msg = ((Buffer)m).getByteBuf();
//                                    System.out.println(ByteBufUtil.prettyHexDump( msg ));
                                    byte[] auth = Native41Authenticator.encode("123456".getBytes(), seed);
                                    int headlength = msg.readMediumLE();
                                    int seq = msg.readByte();
                                    msg.skipBytes(2+2+4+1+23);
                                    String user = BufferUtils.readNullTerminatedString(msg, Charset.forName("utf-8"));
                                    int length = msg.readByte();
                                    byte[] challenge = new byte[length];
                                    msg.readBytes(challenge);
                                    Buffer response = Buffer.buffer();
                                    if(Arrays.equals(auth, challenge)) {
                                        response.appendBytes(new byte[]{0x07,0x00,0x00,0x02,0x00,0x00,0x00,0x02,0x00,0x00,0x00});
                                        socket.write(response);
                                        status = 1;
                                    } else {
                                    	Buffer buf = Buffer.buffer();
                                        buf.appendBytes(new byte[]{(byte)0xff});
                                        String str="#28000Access denied for user 'root'@'localhost' (using password: YES)";
                                        buf.appendShort((short) str.length() );
                                        buf.appendString(str);
                                        
                                        response.appendMediumLE(buf.length());
                                        response.appendByte((byte) 2);
                                        response.appendBuffer(buf);
//			System.out.println("s-c:"+ByteBufUtil.prettyHexDump(response.getByteBuf()));
                                        socket.write(response);
                                        msg.release();
                                        socket.close();
                                    }

                                }
                                }
                        });

                        socket.write(bytes);

                    }
            ).listen(3307, listenResult -> {//代理服务器的监听端口
                if (listenResult.succeeded()) {
                    //成功启动代理服务器
                    logger.info("Mysql proxy server start up.");
                } else {
                    //启动代理服务器失败
                    logger.error("Mysql proxy exit. because: " + listenResult.cause().getMessage(), listenResult.cause());
                    System.exit(1);
                }
            });
        }

        private int initCapabilitiesFlags() {
            int capabilitiesFlags = CapabilitiesFlag.CLIENT_SUPPORTED_CAPABILITIES_FLAGS;
//        if (database != null && !database.isEmpty()) {
            capabilitiesFlags |= CapabilitiesFlag.CLIENT_CONNECT_WITH_DB;
//        }
//        if (connectionAttributes != null && !connectionAttributes.isEmpty()) {
            if (false) {
                capabilitiesFlags |= CapabilitiesFlag.CLIENT_CONNECT_ATTRS;
            }
//        if (!useAffectedRows) {
            capabilitiesFlags |= CapabilitiesFlag.CLIENT_FOUND_ROWS;
//        }

            return capabilitiesFlags;
        }
    }

    public static class MysqlProxyConnection {
        private final Channel clientSocket;
        private final NetSocket serverSocket;
        private final NettyChannelPool channelPool;

        public MysqlProxyConnection(NetSocket serverSocket,Channel clientSocket, NettyChannelPool channelPool) {
            this.clientSocket = clientSocket;
            this.serverSocket = serverSocket;
            this.channelPool = channelPool;
            MysqlHandler handler = (MysqlHandler)clientSocket.pipeline().get("mysqlhandler");
            handler.setServerSocket(serverSocket);
        }

        private void proxy() {
            //当代理与mysql服务器连接关闭时，关闭client与代理的连接
//            serverSocket.closeHandler(v -> clientSocket.close());
                serverSocket.closeHandler(
                		v->{
                			channelPool.release(clientSocket);
                		}
                );
            //不管那端的连接出现异常时，关闭两端的连接
            serverSocket.exceptionHandler(e -> {
            	System.out.println("client closed");
            	e.printStackTrace();
                logger.error(e.getMessage(), e);
                close();
            });
           
             
            //当收到来自mysql目标服务器的数据包时，转发给客户端
            serverSocket.handler(buffer -> {
                
                int pkgLength = buffer.getMediumLE(0);
                int seqId = buffer.getByte(3);
                int command = buffer.getByte(4);
//                System.out.println(String.format("%s %s %s",pkgLength,seqId,command) );
                if(command== CommandType.COM_QUIT) {
                    close();
                    return;
                }
                if(command ==CommandType.COM_QUERY ) {
                    System.out.println( ByteBufUtil.prettyHexDump(buffer.getByteBuf()));
                    System.out.println( buffer.getString(5,buffer.length()));

                }
//                System.out.println(ByteBufUtil.prettyHexDump(buffer.getByteBuf()));
//                clientSocket.writeAndFlush(buffer.getByteBuf());
//                System.out.println(clientSocket.pipeline());
//                ByteBuf bff = clientSocket.pipeline().context("mysqlhandler").alloc().buffer();
//                bff.writeBytes(buffer.getBytes());
                if(clientSocket.isActive() ) {
                	ByteBuf bff = clientSocket.pipeline().context("mysqlhandler").alloc().buffer();
                	bff.writeBytes(buffer.getBytes());
                	clientSocket.writeAndFlush(bff);
                }
            });
        }

        private void close() {
            serverSocket.close();
        }
    }
}