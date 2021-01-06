package com.maxleap.mysqlproxy.async.pool;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class ChannelAttributes {
	 private static final AttributeKey<String> CONNECTION_ID = AttributeKey.newInstance( "connectionId" );
    private static final AttributeKey<String> POOL_ID = AttributeKey.newInstance( "poolId" );
    private static final AttributeKey<Long> CREATION_TIMESTAMP = AttributeKey.newInstance( "creationTimestamp" );
    private static final AttributeKey<Long> LAST_USED_TIMESTAMP = AttributeKey.newInstance( "lastUsedTimestamp" );
    private static final AttributeKey<InetSocketAddress> ADDRESS = AttributeKey.newInstance( "serverAddress" );
    
    

    private ChannelAttributes()
    {
    }

    public static String connectionId( Channel channel )
    {
        return get( channel, CONNECTION_ID );
    }

    public static void setConnectionId( Channel channel, String id )
    {
        setOnce( channel, CONNECTION_ID, id );
    }

    public static String poolId( Channel channel )
    {
        return get( channel, POOL_ID );
    }

    public static void setPoolId( Channel channel, String id )
    {
        setOnce( channel, POOL_ID, id );
    }
    public static InetSocketAddress serverAddress(Channel channel ) {
    	return	get(channel,ADDRESS);
    }
    
    public static void creationServerAddress(Channel channel,InetSocketAddress adress ) {
    	setOnce(channel,ADDRESS,adress);
    }
    
    public static long creationTimestamp( Channel channel )
    {
        return get( channel, CREATION_TIMESTAMP );
    }

    public static void setCreationTimestamp( Channel channel, long creationTimestamp )
    {
        setOnce( channel, CREATION_TIMESTAMP, creationTimestamp );
    }

    public static Long lastUsedTimestamp( Channel channel )
    {
        return get( channel, LAST_USED_TIMESTAMP );
    }

    public static void setLastUsedTimestamp( Channel channel, long lastUsedTimestamp )
    {
        set( channel, LAST_USED_TIMESTAMP, lastUsedTimestamp );
    }


    private static <T> T get( Channel channel, AttributeKey<T> key )
    {
        return channel.attr( key ).get();
    }

    private static <T> void set( Channel channel, AttributeKey<T> key, T value )
    {
        channel.attr( key ).set( value );
    }

    private static <T> void setOnce( Channel channel, AttributeKey<T> key, T value )
    {
        T existingValue = channel.attr( key ).setIfAbsent( value );
        if ( existingValue != null )
        {
            throw new IllegalStateException(
                    "Unable to set " + key.name() + " because it is already set to " + existingValue );
        }
    }
}
