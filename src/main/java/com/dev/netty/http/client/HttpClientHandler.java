package com.dev.netty.http.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HttpClientHandler extends ChannelInboundHandlerAdapter{
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
	}
	
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
		ctx.close();
	}
	
}
