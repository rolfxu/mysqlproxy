import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.maxleap.mysqlproxy.async.pool.ChannelConnector;
import com.maxleap.mysqlproxy.async.pool.ChannelConnectorImpl;
import com.maxleap.mysqlproxy.async.pool.NettyChannelPool;
import com.maxleap.mysqlproxy.async.pool.NettyChannelPoolHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TestPool {
	public static void main(String[] args) {
		 
		InetSocketAddress address = new InetSocketAddress("localhost", 3306);
		ChannelConnector connector = new ChannelConnectorImpl();
		Bootstrap bootstrap = newBootstrap(new NioEventLoopGroup() );
		NettyChannelPoolHandler nettyChannelTracker = new NettyChannelPoolHandler(bootstrap.config().group().next());
		NettyChannelPool pool =    new NettyChannelPool( address, connector, bootstrap, nettyChannelTracker, ChannelHealthChecker.ACTIVE, 10,100 );
		for(int i=0;i<1000;i++) {
			
			CompletionStage<Channel> cs = pool.acquire();
			cs.handle( (channel,error)->{

				if( error!=null ) {
					error.printStackTrace();
				}
				
				pool.release(channel);
				return channel;
			} );
		}
		
		
	}
	
	 public static Bootstrap newBootstrap( EventLoopGroup eventLoopGroup )
	    {
	        Bootstrap bootstrap = new Bootstrap();
	        bootstrap.group( eventLoopGroup );
	        bootstrap.channel(  NioSocketChannel.class );
	        bootstrap.option( ChannelOption.SO_KEEPALIVE, true );
	        bootstrap.option( ChannelOption.SO_REUSEADDR, true );
	        return bootstrap;
	    }
	 
	 
	 
}
