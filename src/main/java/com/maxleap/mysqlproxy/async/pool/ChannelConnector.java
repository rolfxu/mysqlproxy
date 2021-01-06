package com.maxleap.mysqlproxy.async.pool;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

 

public interface ChannelConnector
{
    ChannelFuture connect( InetSocketAddress address, Bootstrap bootstrap );
}