package com.dev.netty.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
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

import com.dev.utils.PropertiesUtil;

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
	
	
	private EventLoopGroup bossGroup = null;
	private EventLoopGroup workerGroup = null;
	
	
	//是否使用https协议
	private static final boolean SSL = System.getProperty("ssl") != null;
	private static int PORT;
	private static String PROFILE = "nettyserver.properties";
	private static String SSLPORT_KEY = "netty.httpserver.ssl.port";
	private static String HTTPPORT_KEY = "netty.httpserver.port";
	private static String BACKLOG_KEY = "netty.sobacklog";
	private static String KEEPALIVE_KEY = "netty.sokeepalive";
	
	/**
	 * 
	 * @Description: 初始化线程组
	 * @author liyut
	 * @version 1.0 
	 * @date 2017年2月10日 上午11:12:19  
	 * @return void
	 */
	public void initEventLoopGroup(){
		//Nio 线程组
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		String sslPort = PropertiesUtil.getValue(SSLPORT_KEY, PROFILE);
		String httpPort = PropertiesUtil.getValue(HTTPPORT_KEY, PROFILE);
		PORT = Integer.parseInt(System.getProperty("port", SSL ? sslPort : httpPort));
	}

	
	public void start()throws Exception{
		SslContext sslContext = null;
		logger.info("SSL证书："+System.getProperty("ssl")); 
		if(SSL){
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		}else{
			sslContext = null;
		}
		initEventLoopGroup();
		//获取配置属性值
		int sobacklog = Integer.parseInt(PropertiesUtil.getValue(BACKLOG_KEY, PROFILE));
		boolean sokeepalive = Boolean.valueOf(PropertiesUtil.getValue(KEEPALIVE_KEY, PROFILE));
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.localAddress(new InetSocketAddress(PORT))
			.handler(new LoggingHandler(LogLevel.INFO))//跟踪日志
			.option(ChannelOption.SO_BACKLOG, sobacklog)
			.option(ChannelOption.SO_KEEPALIVE, sokeepalive)//设置长连接
			.childHandler(new ServletChannelInitializer(sslContext));
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
	 * @Description: 
	 * @author liyut
	 * @version 1.0 
	 * @date 2017年2月10日 上午11:08:35 
	 * @throws Exception 
	 * @return void
	 */
	public void shutdown()throws Exception{
		initEventLoopGroup();
		try {
			logger.info("关闭服务成功");
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		} catch (Exception e) {
			logger.error("关闭服务失败");
			logger.error(e.getMessage());
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
