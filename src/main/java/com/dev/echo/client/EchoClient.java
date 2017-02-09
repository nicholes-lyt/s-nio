package com.dev.echo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;


/**
 * 
 * @ClassName: EchoClient   
 * @Description: 客户端
 * @author liyut
 * @version 1.0 
 * @date 2017年2月5日 下午2:22:19
 */
public class EchoClient {
	
	private static final Logger logger = Logger.getLogger(EchoClient.class);
	
	private String host;
	private int port;
	
	public EchoClient(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	public void start()throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			//创建 Bootstrap
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group)//指定 EventLoopGroup 来处理客户端事件。由于我们使用 NIO 传输，所以用到了 NioEventLoopGroup 的实现
			.channel(NioSocketChannel.class)//使用的 channel 类型是一个用于 NIO 传输
			.remoteAddress(new InetSocketAddress(host, port))//设置服务器的 InetSocketAddress
			.handler(new ChannelInitializer<SocketChannel>(){//当建立一个连接和一个新的通道时，创建添加到 EchoClientHandler 实例 到 channel pipeline
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new EchoClientHandler());
				}
			});
			ChannelFuture future = bootstrap.connect().sync();//连接到远程;等待连接完成
			future.channel().closeFuture().sync();//阻塞直到 Channel 关闭
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();//
		}
	}
	
	public static void main(String[] args) throws Exception {
		String host = "127.0.0.1";
		int port = 9999;
		if (args != null && args.length == 2) {
			logger.info("Usage: " + EchoClient.class.getSimpleName() + " <host> <port>");
			host = args[0];
			port = Integer.parseInt(args[1]);
			return;
		}
		new EchoClient(host, port).start();
	}
}
