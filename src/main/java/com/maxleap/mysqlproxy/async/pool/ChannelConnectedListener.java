package com.maxleap.mysqlproxy.async.pool;


import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class ChannelConnectedListener implements ChannelFutureListener {
    
	private final InetSocketAddress address;
    
	private final ChannelPromise handshakeCompletedPromise;
	
	private final ChannelFuture channelConnected;
	private final ChannelPromise connectionInitialized;
	
	public ChannelConnectedListener(InetSocketAddress address, ChannelPromise handshakeCompleted,
			ChannelFuture channelConnected, ChannelPromise connectionInitialized) {
        this.address = address;
        this.handshakeCompletedPromise = handshakeCompleted;
        this.channelConnected = channelConnected;
        this.connectionInitialized = connectionInitialized;
	}

 

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		 Channel channel = future.channel();
		 if(future.isSuccess()) {
			ChannelPipeline pipeline = channel.pipeline();
			pipeline.remove(ReadTimeoutHandler.class);
            pipeline.addLast( new HandshakeHandler(  handshakeCompletedPromise,connectionInitialized ) );
//            channel.writeAndFlush( handshakeBuf(), channel.voidPromise() );
		 } else {
			 future.cause().printStackTrace();
			 connectionInitialized.setFailure(  future.cause()  );
		 }
	}

}
