
package com.maxleap.mysqlproxy.async.pool;

import io.netty.channel.Channel;

import java.util.concurrent.CompletionStage;

public interface ExtendedChannelPool
{
    CompletionStage<Channel> acquire();

    CompletionStage<Void> release( Channel channel );

    boolean isClosed();

    String id();

    CompletionStage<Void> close();
}

