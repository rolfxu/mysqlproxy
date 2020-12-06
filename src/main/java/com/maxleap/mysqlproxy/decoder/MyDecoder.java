package com.maxleap.mysqlproxy.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import io.vertx.mysqlclient.impl.MySQLCollation;
import io.vertx.mysqlclient.impl.protocol.CapabilitiesFlag;
import io.vertx.mysqlclient.impl.util.BufferUtils;
import io.vertx.mysqlclient.impl.util.CachingSha2Authenticator;
import io.vertx.mysqlclient.impl.util.Native41Authenticator;
import io.vertx.sqlclient.impl.command.CommandResponse;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static io.vertx.mysqlclient.impl.protocol.CapabilitiesFlag.*;
import static io.vertx.mysqlclient.impl.protocol.Packets.*;

public class MyDecoder extends ByteToMessageCodec {
    protected static final int NONCE_LENGTH = 20;
    private static final int AUTH_PLUGIN_DATA_PART1_LENGTH = 8;
    protected byte[] authPluginData;
    private static final int ST_CONNECTING = 0;
    private static final int ST_AUTHENTICATING = 1;
    private static final int ST_CONNECTED = 2;

    protected static final int AUTH_SWITCH_REQUEST_STATUS_FLAG = 0xFE;
    protected static final int AUTH_MORE_DATA_STATUS_FLAG = 0x01;
    protected static final int AUTH_PUBLIC_KEY_REQUEST_FLAG = 0x02;
    protected static final int FAST_AUTH_STATUS_FLAG = 0x03;
    protected static final int FULL_AUTHENTICATION_STATUS_FLAG = 0x04;

    private int status = ST_CONNECTING;



    private CompositeByteBuf aggregatedPacketPayload = null;
    private ChannelHandlerContext ctx;
    int sequenceId = 1;
    public Handler<NetSocket> completionHandler;
    public NetSocket serverSocket;
    public NetSocket clientSocket;
    public MyDecoder(NetSocket socket){
        this.clientSocket = socket;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
//        ByteBuf b = (ByteBuf)msg;
//        byte[] bytes = new byte[b.readableBytes()];
//        b.readBytes( bytes );
//        serverSocket.write(Buffer.buffer().appendBytes(bytes) );
        System.out.println(ByteBufUtil.prettyHexDump((ByteBuf)msg));
         out.writeBytes((ByteBuf)msg);
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
        System.out.println(ByteBufUtil.prettyHexDump(in));

        this.ctx = ctx;
        if (in.readableBytes() > 4) {
            if(status==ST_CONNECTED) {
                serverSocket.write(Buffer.buffer(in));
                return;
            }
            int packetStartIdx = in.readerIndex();
            int payloadLength = in.readUnsignedMediumLE();
            int sequenceId = in.readUnsignedByte();

            if (payloadLength >= PACKET_PAYLOAD_LENGTH_LIMIT && aggregatedPacketPayload == null) {
                aggregatedPacketPayload = ctx.alloc().compositeBuffer();
            }

            // payload
            if (in.readableBytes() >= payloadLength) {
                if (aggregatedPacketPayload != null) {
                    // read a split packet
                    aggregatedPacketPayload.addComponent(true, in.readRetainedSlice(payloadLength));

                    if (payloadLength < PACKET_PAYLOAD_LENGTH_LIMIT) {
                        // we have just read the last split packet and there will be no more split packet
                        try {
                            decode00(aggregatedPacketPayload, aggregatedPacketPayload.readableBytes());
                        } finally {
                            ReferenceCountUtil.release(aggregatedPacketPayload);
                            aggregatedPacketPayload = null;
                        }
                    }
                } else {
                    // read a non-split packet
                    decode00(in.readSlice(payloadLength), payloadLength);
                }
            } else {
                in.readerIndex(packetStartIdx);
            }
        }
    }

    protected void decode00(ByteBuf payload, int payloadLength) {
        this.ctx = ctx;

        switch (status) {
            case ST_CONNECTING:
                handleInitialHandshake(payload);
                status = ST_AUTHENTICATING;
                break;
            case ST_AUTHENTICATING:
                handleAuthentication(payload);
                status = ST_CONNECTED;
                break;
        }


        }
    private void handleInitialHandshake(ByteBuf payload){
        short protocolVersion = payload.readUnsignedByte();

        String serverVersion = BufferUtils.readNullTerminatedString(payload, StandardCharsets.US_ASCII);
        // we assume the server version follows ${major}.${minor}.${release} in https://dev.mysql.com/doc/refman/8.0/en/which-version.html
        String[] versionNumbers = serverVersion.split("\\.");
        int majorVersion = Integer.parseInt(versionNumbers[0]);
        int minorVersion = Integer.parseInt(versionNumbers[1]);
        // we should truncate the possible suffixes here
        String releaseVersion = versionNumbers[2];
        int releaseNumber;
        int indexOfFirstSeparator = releaseVersion.indexOf("-");
        if (indexOfFirstSeparator != -1) {
            // handle unstable release suffixes
            String releaseNumberString = releaseVersion.substring(0, indexOfFirstSeparator);
            releaseNumber = Integer.parseInt(releaseNumberString);
        } else {
            releaseNumber = Integer.parseInt(versionNumbers[2]);
        }
        if (majorVersion == 5 && (minorVersion < 7 || (minorVersion == 7 && releaseNumber < 5))) {
            // EOF_HEADER is enabled
        }

        long connectionId = payload.readUnsignedIntLE();

        // read first part of scramble
        this.authPluginData = new byte[NONCE_LENGTH];
        payload.readBytes(authPluginData, 0, AUTH_PLUGIN_DATA_PART1_LENGTH);

        //filler
        payload.readByte();

        // read lower 2 bytes of Capabilities flags
        int lowerServerCapabilitiesFlags = payload.readUnsignedShortLE();

        short characterSet = payload.readUnsignedByte();

        int statusFlags = payload.readUnsignedShortLE();

        // read upper 2 bytes of Capabilities flags
        int capabilityFlagsUpper = payload.readUnsignedShortLE();
        final int serverCapabilitiesFlags = (lowerServerCapabilitiesFlags | (capabilityFlagsUpper << 16));

        // length of the combined auth_plugin_data (scramble)
        short lenOfAuthPluginData;
        boolean isClientPluginAuthSupported = (serverCapabilitiesFlags & CapabilitiesFlag.CLIENT_PLUGIN_AUTH) != 0;
        if (isClientPluginAuthSupported) {
            lenOfAuthPluginData = payload.readUnsignedByte();
        } else {
            payload.readerIndex(payload.readerIndex() + 1);
            lenOfAuthPluginData = 0;
        }

        // 10 bytes reserved
        payload.readerIndex(payload.readerIndex() + 10);

        // Rest of the plugin provided data
        payload.readBytes(authPluginData, AUTH_PLUGIN_DATA_PART1_LENGTH, Math.max(NONCE_LENGTH - AUTH_PLUGIN_DATA_PART1_LENGTH, lenOfAuthPluginData - 9));
        payload.readByte(); // reserved byte

        // we assume the server supports auth plugin
        final String authPluginName = BufferUtils.readNullTerminatedString(payload, StandardCharsets.UTF_8);
        System.out.println(authPluginName);
        doSendHandshakeResponseMessage(authPluginName, authPluginData, serverCapabilitiesFlags);
    }



    private void doSendHandshakeResponseMessage(String authMethodName, byte[] nonce, int serverCapabilitiesFlags) {
        Map<String, String> clientConnectionAttributes =  null;
        sendHandshakeResponseMessage("root", "123456", "es", nonce, authMethodName, clientConnectionAttributes);
    }
    private void handleAuthentication(ByteBuf payload) {
        int header = payload.getUnsignedByte(payload.readerIndex());
        switch (header) {
            case OK_PACKET_HEADER:
                status = ST_CONNECTED;
                completionHandler.handle(clientSocket);
                break;
            case ERROR_PACKET_HEADER:
//                handleErrorPacketPayload(payload);
                break;
            case AUTH_SWITCH_REQUEST_STATUS_FLAG:
//                handleAuthSwitchRequest(cmd.password().getBytes(StandardCharsets.UTF_8), payload);
                break;
            case AUTH_MORE_DATA_STATUS_FLAG:
//                handleAuthMoreData(cmd.password().getBytes(StandardCharsets.UTF_8), payload);
                break;
            default:
//                completionHandler.handle(CommandResponse.failure(new IllegalStateException("Unhandled state with header: " + header)));
                System.out.println("handleAuth");
        }
    }
    private void sendHandshakeResponseMessage(String username, String password, String database, byte[] nonce, String authMethodName, Map<String, String> clientConnectionAttributes) {
        ByteBuf packet = allocateBuffer();
        // encode packet header
        int packetStartIdx = packet.writerIndex();
        packet.writeMediumLE(0); // will set payload length later by calculation
        packet.writeByte(sequenceId);

        // encode packet payload
        int clientCapabilitiesFlags = initCapabilitiesFlags();
        packet.writeIntLE(clientCapabilitiesFlags);
        packet.writeIntLE(PACKET_PAYLOAD_LENGTH_LIMIT);
        packet.writeByte(MySQLCollation.valueOfName("utf8_general_ci").collationId());
        packet.writeZero(23); // filler
        BufferUtils.writeNullTerminatedString(packet, username, StandardCharsets.UTF_8);
        if (password.isEmpty()) {
            packet.writeByte(0);
        } else {
            byte[] scrambledPassword;
            switch (authMethodName) {
                case "mysql_native_password":
                    scrambledPassword = Native41Authenticator.encode(password.getBytes(StandardCharsets.UTF_8), nonce);
                    break;
                case "caching_sha2_password":
                    scrambledPassword = CachingSha2Authenticator.encode(password.getBytes(StandardCharsets.UTF_8), nonce);
                    break;
                default:
//                    completionHandler.handle(CommandResponse.failure(new UnsupportedOperationException("Unsupported authentication method: " + authMethodName)));
                    return;
            }
            if ((clientCapabilitiesFlags & CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA) != 0) {
                BufferUtils.writeLengthEncodedInteger(packet, scrambledPassword.length);
                packet.writeBytes(scrambledPassword);
            } else if ((clientCapabilitiesFlags & CLIENT_SECURE_CONNECTION) != 0) {
                packet.writeByte(scrambledPassword.length);
                packet.writeBytes(scrambledPassword);
            } else {
                packet.writeByte(0);
            }
        }
        if ((clientCapabilitiesFlags & CLIENT_CONNECT_WITH_DB) != 0) {
            BufferUtils.writeNullTerminatedString(packet, database, StandardCharsets.UTF_8);
        }
        if ((clientCapabilitiesFlags & CLIENT_PLUGIN_AUTH) != 0) {
            BufferUtils.writeNullTerminatedString(packet, authMethodName, StandardCharsets.UTF_8);
        }
        if ((clientCapabilitiesFlags & CLIENT_CONNECT_ATTRS) != 0) {
            encodeConnectionAttributes(clientConnectionAttributes, packet);
        }

        // set payload length
        int payloadLength = packet.writerIndex() - packetStartIdx - 4;
        packet.setMediumLE(packetStartIdx, payloadLength);

        sendPacket(packet, payloadLength);
    }

    void sendPacket(ByteBuf packet, int payloadLength) {
        if (payloadLength >= PACKET_PAYLOAD_LENGTH_LIMIT) {
      /*
         The original packet exceeds the limit of packet length, split the packet here.
         if payload length is exactly 16MBytes-1byte(0xFFFFFF), an empty packet is needed to indicate the termination.
       */
            sendSplitPacket(packet);
        } else {
            sendNonSplitPacket(packet);
        }
    }

    private void sendSplitPacket(ByteBuf packet) {
        ByteBuf payload = packet.skipBytes(4);
        while (payload.readableBytes() >= PACKET_PAYLOAD_LENGTH_LIMIT) {
            // send a packet with 0xFFFFFF length payload
            ByteBuf packetHeader = allocateBuffer(4);
            packetHeader.writeMediumLE(PACKET_PAYLOAD_LENGTH_LIMIT);
            packetHeader.writeByte(sequenceId++);
            ctx.write(packetHeader);
            ctx.write(payload.readRetainedSlice(PACKET_PAYLOAD_LENGTH_LIMIT));
        }

        // send a packet with last part of the payload
        ByteBuf packetHeader = allocateBuffer(4);
        packetHeader.writeMediumLE(payload.readableBytes());
        packetHeader.writeByte(sequenceId++);
        ctx.write(packetHeader);
        ctx.writeAndFlush(payload);
    }

    void sendNonSplitPacket(ByteBuf packet) {
        sequenceId++;
        ctx.writeAndFlush(packet);
    }

    ByteBuf allocateBuffer() {
        return ctx.alloc().ioBuffer();
    }

    ByteBuf allocateBuffer(int capacity) {
        return ctx.alloc().ioBuffer(capacity);
    }

    private int initCapabilitiesFlags() {
        int capabilitiesFlags = CLIENT_SUPPORTED_CAPABILITIES_FLAGS;
//        if (database != null && !database.isEmpty()) {
            capabilitiesFlags |= CLIENT_CONNECT_WITH_DB;
//        }
//        if (connectionAttributes != null && !connectionAttributes.isEmpty()) {
        if (false) {
            capabilitiesFlags |= CLIENT_CONNECT_ATTRS;
        }
//        if (!useAffectedRows) {
            capabilitiesFlags |= CLIENT_FOUND_ROWS;
//        }

        return capabilitiesFlags;
    }

    protected final void encodeConnectionAttributes(Map<String, String> clientConnectionAttributes, ByteBuf packet) {
        ByteBuf kv = allocateBuffer();
        for (Map.Entry<String, String> attribute : clientConnectionAttributes.entrySet()) {
            BufferUtils.writeLengthEncodedString(kv, attribute.getKey(), StandardCharsets.UTF_8);
            BufferUtils.writeLengthEncodedString(kv, attribute.getValue(), StandardCharsets.UTF_8);
        }
        BufferUtils.writeLengthEncodedInteger(packet, kv.readableBytes());
        packet.writeBytes(kv);
    }
}
