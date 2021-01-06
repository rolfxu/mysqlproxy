package com.maxleap.mysqlproxy.async.pool;

 
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.util.concurrent.EventExecutor;

public class NettyChannelPoolHandler implements ChannelPoolHandler{
	
	private Logger log = LoggerFactory.getLogger(NettyChannelPoolHandler.class);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    private final Map<InetSocketAddress,Integer> addressToInUseChannelCount = new HashMap<>();
    private final Map<InetSocketAddress,Integer> addressToIdleChannelCount = new HashMap<>();
    
    private final Lock read = lock.readLock();
    private final Lock write = lock.writeLock();
    private final ChannelGroup allChannels;
    private final ChannelFutureListener closeListener = future -> channelClosed( future.channel() );

    public NettyChannelPoolHandler(  EventExecutor eventExecutor  )
    {
        this(  new DefaultChannelGroup( "all-connections", eventExecutor ) );
    }

    public NettyChannelPoolHandler(   ChannelGroup channels  )
    {
        this.allChannels = channels;
    }

    private void doInWriteLock( Runnable work )
    {
        try
        {
            write.lock();
            work.run();
        }
        finally
        {
            write.unlock();
        }
    }

    private <T> T retrieveInReadLock( Supplier<T> work )
    {
        try
        {
            read.lock();
            return work.get();
        }
        finally
        {
            read.unlock();
        }
    }

    @Override
    public void channelReleased( Channel channel )
    {
        doInWriteLock( () ->
        {
            decrementInUse( channel );
            incrementIdle( channel );
        } );

        channel.closeFuture().addListener( closeListener );
        log.debug( "Channel [0x%s] released back to the pool", channel.id() );
    }

    @Override
    public void channelAcquired( Channel channel )
    {
        doInWriteLock( () ->
        {
            incrementInUse( channel );
            decrementIdle( channel );
        } );

        channel.closeFuture().removeListener( closeListener );
        log.debug( "Channel [0x%s] acquired from the pool. Local address: %s, remote address: %s", channel.id(), channel.localAddress(),
                channel.remoteAddress() );
    }
    
    public void channelCreated( Channel channel  )
    {
        // when it is created, we count it as idle as it has not been acquired out of the pool
        doInWriteLock( () -> incrementIdle( channel ) );

        allChannels.add( channel );
        log.debug( "Channel [0x%s] created. Local address: %s, remote address: %s", channel.id(), channel.localAddress(), channel.remoteAddress() );
    }



    public void channelClosed( Channel channel )
    {
        doInWriteLock( () -> decrementIdle( channel ) );
    }

    public int inUseChannelCount( InetSocketAddress address )
    {
        return retrieveInReadLock( () -> addressToInUseChannelCount.getOrDefault( address, 0 ) );
    }

    public int idleChannelCount( InetSocketAddress address )
    {
        return retrieveInReadLock( () -> addressToIdleChannelCount.getOrDefault( address, 0 ) );
    }

    public void prepareToCloseChannels()
    {
//        for ( Channel channel : allChannels )
//        {
//            BoltProtocol protocol = BoltProtocol.forChannel( channel );
//            try
//            {
//                protocol.prepareToCloseChannel( channel );
//            }
//            catch ( Throwable e )
//            {
//                // only logging it
//                log.debug( "Failed to prepare to close Channel %s due to error %s. " +
//                                "It is safe to ignore this error as the channel will be closed despite if it is successfully prepared to close or not.", channel,
//                        e.getMessage() );
//            }
//        }
    }

    private void incrementInUse( Channel channel )
    {
        increment( channel, addressToInUseChannelCount );
    }

    private void decrementInUse( Channel channel )
    {
        decrement( channel, addressToInUseChannelCount );
    }

    private void incrementIdle( Channel channel )
    {
        increment( channel, addressToIdleChannelCount );
    }

    private void decrementIdle( Channel channel )
    {
        decrement( channel, addressToIdleChannelCount );
    }

    private void increment( Channel channel, Map<InetSocketAddress,Integer> countMap )
    {
    	InetSocketAddress address = ChannelAttributes.serverAddress( channel );
        Integer count = countMap.computeIfAbsent( address, k -> 0 );
        countMap.put( address, count + 1 );
    }

    private void decrement( Channel channel, Map<InetSocketAddress,Integer> countMap )
    {
    	InetSocketAddress address = ChannelAttributes.serverAddress( channel );
        if ( !countMap.containsKey( address ) )
        {
            throw new IllegalStateException( "No count exist for address '" + address + "'" );
        }
        Integer count = countMap.get( address );
        countMap.put( address, count - 1 );
    }
}
