import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;

import com.maxleap.mysqlproxy.ApplicationServer.MysqlProxyConnection;
import com.maxleap.mysqlproxy.async.pool.ChannelConnector;
import com.maxleap.mysqlproxy.async.pool.ChannelConnectorImpl;
import com.maxleap.mysqlproxy.async.pool.NettyChannelPool;
import com.maxleap.mysqlproxy.async.pool.NettyChannelPoolHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.socket.nio.NioSocketChannel;

public class PoolTest {
	public static void main(String[] args) {
		
		 Bootstrap bootstrap = new Bootstrap();
	        bootstrap.group( new NioEventLoopGroup()  );
	        bootstrap.channel(  NioSocketChannel.class );
	        bootstrap.option( ChannelOption.SO_KEEPALIVE, true );
	        bootstrap.option( ChannelOption.SO_REUSEADDR, true );
         bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
	        
		InetSocketAddress address = new InetSocketAddress("localhost", 3306);
		ChannelConnector connector = new ChannelConnectorImpl();
		NettyChannelPoolHandler nettyChannelTracker = new NettyChannelPoolHandler(bootstrap.config().group().next());
		NettyChannelPool pool =    new NettyChannelPool( address, connector, bootstrap, nettyChannelTracker, ChannelHealthChecker.ACTIVE, 10000,100 );
	
		for(int i=0;i<100;i++) {
			CompletionStage<Channel> stage = pool.acquire();
			try {
				Thread.sleep(1000l);
			} catch (InterruptedException e) {
			}
			stage.handle( (channel,error)->{

				if( error!=null ) {
					System.out.println("errorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerror。");
					return null;
				} 
				pool.release(channel);
				return channel;
			} );
		}
	
	}
}
