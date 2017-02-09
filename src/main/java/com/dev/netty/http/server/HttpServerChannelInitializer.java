package com.dev.netty.http.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

/**
 * 
 * @ClassName: HttpSeverChannelInitializer   
 * @Description: 初始化连接
 * @author liyut
 * @version 1.0 
 * @date 2017年2月9日 上午11:06:13
 */
public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel>{
	
	//SSL认证
	private final SslContext sslContext;
	
	public HttpServerChannelInitializer(SslContext sslContext){
		this.sslContext = sslContext;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		//ssl判断
		if(sslContext != null){
			p.addLast(sslContext.newHandler(ch.alloc()));
		}
		p.addLast(new HttpServerCodec());
		p.addLast(new HttpObjectAggregator(2048));//HTTP 消息的合并处理
		p.addLast(new HttpServerHandler());
	}

}
