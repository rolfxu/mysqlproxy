package com.maxleap.mysqlproxy.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.vertx.core.buffer.Buffer;
import io.vertx.mysqlclient.impl.util.BufferUtils;
import io.vertx.mysqlclient.impl.util.Native41Authenticator;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class ProxyDecoder extends ByteToMessageCodec {


    byte[] seed;

    public ProxyDecoder(byte[] seed){
        this.seed = seed;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List out) throws Exception {

    }
}
