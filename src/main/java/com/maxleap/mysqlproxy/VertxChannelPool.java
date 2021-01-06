package com.maxleap.mysqlproxy;

import com.maxleap.mysqlproxy.backend.MysqlHandler;
import com.maxleap.mysqlproxy.decoder.MyDecoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.NetSocketInternal;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import io.vertx.mysqlclient.impl.codec.MySQLCodec;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class VertxChannelPool {

    private Deque<NetSocket> active = new ConcurrentLinkedDeque<>();

    private NetClient netClient;
    public  VertxChannelPool(NetClient netClient){
        this.netClient = netClient;
    }

    public NetSocket aquire(){
        return active.poll();
    }
    public int getActiveSize(){
        return active.size();
    }

    public void returnPool(NetSocket socket){
        System.out.println("add a mysql socket.");
        active.add(socket);
    }
    public void connect() {
        netClient.connect(3306,"localhost",result->{
            NetSocket socket =  result.result();
            NetSocketInternal socketInternal = (NetSocketInternal)socket;
            MysqlHandler handler = new MysqlHandler(socket);
            handler.completionHandler = response->{
                active.push(response);
                System.out.println(active.size());
            };
            socketInternal.handler(handler);
            socket.exceptionHandler(v->
                    System.out.println("aaa")
            );
            socket.closeHandler(v-> connect());
        });
    }

    public void init(){
        for(int i=0;i<2;i++) {
            connect();
        }

    }



}
