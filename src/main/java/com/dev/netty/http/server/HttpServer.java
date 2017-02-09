package com.dev.netty.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

/**
 * 
 * @ClassName: HttpServerHandler   
 * @Description: http --- 服务端启动
 * @author liyut
 * @version 1.0 
 * @date 2017年2月8日 上午11:50:56
 */
public class HttpServer {
	
	private static final Logger logger = Logger.getLogger(HttpServer.class);
	
	//是否使用https协议
	private static final boolean SSL = System.getProperty("ssl") != null;
	private static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8082"));

	public void start()throws Exception{
		SslContext sslContext;
		logger.info("SSL证书："+System.getProperty("ssl")); 
		if(SSL){
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		}else{
			sslContext = null;
		}
		//Nio 线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.localAddress(new InetSocketAddress(PORT))
			.handler(new LoggingHandler(LogLevel.INFO))//跟踪日志
			.childHandler(new HttpServerChannelInitializer(sslContext));
			//绑定端口
			ChannelFuture f = b.bind(PORT).sync();
			logger.info("HttpServer name is "+HttpServer.class.getName() + " started and listen on " + f.channel().localAddress());
			//监听到端口，关闭
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	/**
	 * 
	 * @Description: 启动服务
	 * @author liyut
	 * @version 1.0 
	 * @date 2017年2月9日 上午11:22:04 
	 * @param args
	 * @throws Exception 
	 * @return void
	 */
	public static void main(String[] args) throws Exception {
		new HttpServer().start();
	}
}