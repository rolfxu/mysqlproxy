package com.maxleap.mysqlproxy.async.pool;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class ChannelConnectorImpl implements ChannelConnector{

	@Override
	public ChannelFuture connect(InetSocketAddress address, Bootstrap bootstrap) {
		
		ChannelFuture channelConnect =  bootstrap.connect(address);
		
		Channel channel = channelConnect.channel();
        ChannelPromise handshakeCompleted = channel.newPromise();
        ChannelPromise connectionInitialized = channel.newPromise();
        
        installChannelConnectedListeners(address, channelConnect, handshakeCompleted,connectionInitialized);
		return connectionInitialized;
	}
	 private void installChannelConnectedListeners( InetSocketAddress address, ChannelFuture channelConnected,
	            ChannelPromise handshakeCompleted,ChannelPromise connectionInitialized )
	    {
	        ChannelPipeline pipeline = channelConnected.channel().pipeline();
	        
	        // add timeout handler to the pipeline when channel is connected. it's needed to limit amount of time code
	        // spends in TLS and Bolt handshakes. prevents infinite waiting when database does not respond
	        channelConnected.addListener( future ->
	                pipeline.addFirst( new ReadTimeoutHandler( 5 ) ) );
	        // add listener that sends Bolt handshake bytes when channel is connected
	        channelConnected.addListener(
	                new ChannelConnectedListener( address,  handshakeCompleted,channelConnected,connectionInitialized ) );
	    }
}
