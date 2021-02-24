package com.maxleap.mysqlproxy.async.pool;
 

import static com.maxleap.mysqlproxy.async.pool.Futures.asCompletionStage;
import static java.util.Objects.requireNonNull;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.internal.PlatformDependent;
 

public class NettyChannelPool implements ExtendedChannelPool
{
    /**
     * Unlimited amount of parties are allowed to request channels from the pool.
     */
    private static final int MAX_PENDING_ACQUIRES = 10000;
    /**
     * Do not check channels when they are returned to the pool.
     */
    private static final boolean RELEASE_HEALTH_CHECK = true;

    private final FixedChannelPool delegate;
    private final AtomicBoolean closed = new AtomicBoolean( false );
    private final String id;
    private final CompletableFuture<Void> closeFuture = new CompletableFuture<>();
    private MetricRegistry registry;
    public <T> NettyChannelPool(  InetSocketAddress address, ChannelConnector connector, Bootstrap bootstrap, NettyChannelPoolHandler handler,
            ChannelHealthChecker healthCheck, long acquireTimeoutMillis, int maxConnections )
    {
    	
        requireNonNull( address );
        requireNonNull( connector );
        requireNonNull( handler );
        this.id = poolId( address );
        this.delegate = new FixedChannelPool( bootstrap, handler, healthCheck, FixedChannelPool.AcquireTimeoutAction.FAIL, acquireTimeoutMillis, maxConnections,
                MAX_PENDING_ACQUIRES, RELEASE_HEALTH_CHECK )
        {
            @Override
            protected ChannelFuture connectChannel( Bootstrap bootstrap )
            {
                ChannelFuture channelFuture = connector.connect( address, bootstrap );
                channelFuture.addListener( future -> {
                    if ( future.isSuccess() )
                    {
                        // notify pool handler about a successful connection
                        Channel channel = channelFuture.channel();
                        ChannelAttributes.setPoolId( channel, id );
                        handler.channelCreated( channel );
                        
                    }
                    else
                    {
                        System.out.println("sadfasdf");  
                    }
                } );
                return channelFuture;
            }
        };
        registry = new MetricRegistry();
        registry.register("queue.queuecount", new Gauge<String>() {
        	@Override
        	public String getValue() {
//        		for(Channel c:NettyChannelPool.this.delegate.getDeque()) {
//        			System.out.println(c.isOpen()+"-"+c.isActive()+"/"+c);
//        		}
        		
        		return PlatformDependent.usedDirectMemory()+"/"+PlatformDependent.maxDirectMemory();
        	}
		} );
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(5, TimeUnit.SECONDS);
    }

    @Override
    public CompletionStage<Void> close()
    {
        if ( closed.compareAndSet( false, true ) )
        {
            asCompletionStage( delegate.closeAsync(), closeFuture );
        }
        return closeFuture;
    }

    @Override
    public CompletionStage<Channel> acquire()
    {
        return asCompletionStage( delegate.acquire() );
    }

    @Override
    public CompletionStage<Void> release( Channel channel )
    {
        return asCompletionStage( delegate.release( channel ) );
    }

    @Override
    public boolean isClosed()
    {
        return closed.get();
    }

    @Override
    public String id()
    {
        return this.id;
    }

    private String poolId( InetSocketAddress serverAddress )
    {
        return String.format( "%s:%d-%d", serverAddress.getAddress().getHostAddress(), serverAddress.getPort(), this.hashCode() );
    }
}
