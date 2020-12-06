package com.maxleap.mysqlproxy;

import com.maxleap.mysqlproxy.decoder.MyDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.impl.NetServerImpl;
import io.vertx.core.net.impl.NetSocketImpl;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.mysqlclient.impl.protocol.CapabilitiesFlag;
import io.vertx.mysqlclient.impl.protocol.CommandType;
import io.vertx.mysqlclient.impl.util.BufferUtils;
import io.vertx.mysqlclient.impl.util.Native41Authenticator;

import java.nio.charset.Charset;
import java.security.Security;
import java.util.Arrays;

public class MysqlProxyServer {
    private static final Logger logger = LoggerFactory.getLogger(MysqlProxyServer.class);

    public static void main(String[] args) {
        Vertx.vertx(new VertxOptions().setMetricsOptions(
                new DropwizardMetricsOptions().setEnabled(true).setJmxEnabled(true)
        )).deployVerticle(new MysqlProxyServerVerticle());
    }

    public static class MysqlProxyServerVerticle extends AbstractVerticle {
        private final int port = 3306;
        private final String mysqlHost = "localhost";

        @Override
        public void start() throws Exception {
            NetServer netServer = vertx.createNetServer();//创建代理服务器
            NetClient netClient = vertx.createNetClient();//创建连接mysql客户端
            VertxChannelPool channelPool = new VertxChannelPool(netClient);
            vertx.setPeriodic(1000,id->{
                System.out.println(channelPool.getActiveSize());
            });
            channelPool.init();
            netServer.connectHandler(
                    socket->{
                        int status =0;
                        System.out.println("connected the server");
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
                                    for(;;){
                                        NetSocket clientSocket=  channelPool.aquire();

                                        System.out.println(clientSocket);
                                        if ( ((NetSocketImpl)clientSocket).channel().isActive() ){
                                            clientSocket.write(m);
                                            new MysqlProxyConnection(socket,clientSocket,channelPool).proxy();
                                            break;
                                        } else {
                                            System.out.println("the connection is closed");
                                        }
                                    }


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
                                        System.out.println( ((NetSocketImpl)socket).remoteAddress() );
                                        String str="#28000Access denied for user 'root'@'localhost' (using password: YES)";
                                        buf.appendShort((short) str.length() );
                                        buf.appendString(str);
                                        response.appendMediumLE(buf.length());
                                        response.appendByte((byte) 1);
                                        response.appendBuffer(buf);
			System.out.println(ByteBufUtil.prettyHexDump(response.getByteBuf()));
                                        socket.write(response);
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
        private final NetSocket clientSocket;
        private final NetSocket serverSocket;
        private final VertxChannelPool channelPool;

        public MysqlProxyConnection(NetSocket serverSocket,NetSocket clientSocket, VertxChannelPool channelPool) {
            this.clientSocket = clientSocket;
            this.serverSocket = serverSocket;
            this.channelPool = channelPool;
        }

        private void proxy() {
            //当代理与mysql服务器连接关闭时，关闭client与代理的连接
//            serverSocket.closeHandler(v -> clientSocket.close());
                serverSocket.closeHandler(v->channelPool.returnPool(clientSocket));
            //不管那端的连接出现异常时，关闭两端的连接
            serverSocket.exceptionHandler(e -> {
                logger.error(e.getMessage(), e);
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                close();
            });
            clientSocket.exceptionHandler(e -> {
                logger.error(e.getMessage(), e);
                System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbb");
                close();
            });
            //当收到来自客户端的数据包时，转发给mysql目标服务器
            clientSocket.handler(buffer -> {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>");
                serverSocket.write(buffer);
            });
            //当收到来自mysql目标服务器的数据包时，转发给客户端
            serverSocket.handler(buffer -> {
//                System.out.println(ByteBufUtil.prettyHexDump(buffer.getByteBuf()));
                int pkgLength = buffer.getMediumLE(0);
                int seqId = buffer.getByte(3);
                int command = buffer.getByte(4);
//                System.out.println(String.format("%s %s %s",pkgLength,seqId,command) );
                if(command== CommandType.COM_QUIT) {
                    close();
                    return;
                }
                clientSocket.write(buffer);

            });
        }

        private void close() {
            serverSocket.close();
        }
    }
}