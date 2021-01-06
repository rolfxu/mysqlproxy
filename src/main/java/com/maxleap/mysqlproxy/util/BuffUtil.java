package com.maxleap.mysqlproxy.util;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class BuffUtil {
	
	public static String readString(ByteBuf byteBuf) {
		int length = byteBuf.readableBytes();
		int readIndex = byteBuf.readerIndex();
		int i= readIndex;
		int num=0;
		while( i< readIndex+length) {
			if(byteBuf.getByte(i) == 0 ) {
				ByteBuf buf = byteBuf.readBytes(num);
				byteBuf.skipBytes(1);
				try{
					String rst =  buf.toString( Charset.forName("US-ASCII"));
					return rst;
				}finally{
					buf.release();
				}
			}
			num++;
			i++;
		}
		return null;
		
	}

	public static String readString(ByteBuf byteBuf, int length) {
		if( byteBuf.readableBytes()<length){
			return null;
		}
	
		ByteBuf buf = byteBuf.readBytes(length);
		try{
			return  buf.toString( Charset.forName("US-ASCII"));
		}finally{
			buf.release();
		}
		
	}
}
