package com.dev.echo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

/**
 * 
 * @ClassName: EchoServer   
 * @Description: 服务端
 * @author liyut
 * @version 1.0 
 * @date 2017年2月5日 上午11:44:14
 */
public class EchoServer {
	
	private static final Logger logger = Logger.getLogger(EchoServer.class);
	
	//端口
	private int port;

	public EchoServer (int port){
		this.port = port;
	}
	
	public void start() throws Exception{
		//创建 EventLoopGroup
		NioEventLoopGroup group = new NioEventLoopGroup();
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(group)
			.channel(NioServerSocketChannel.class)	//指定使用 NIO 的传输 Channel
			.localAddress(new InetSocketAddress(port))//设置 socket 地址使用所选的端口
			.childHandler(new ChannelInitializer<SocketChannel>(){//添加 EchoServerHandler 到 Channel 的 ChannelPipeline
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new EchoServerHandler());
				}
			});
			ChannelFuture future = serverBootstrap.bind().sync();//绑定的服务器;sync 等待服务器关闭
			logger.info("server name is "+EchoServer.class.getName() + " started and listen on " + future.channel().localAddress());
			future.channel().closeFuture().sync();//关闭 channel块，直到它被关闭
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully().sync();//关机的 EventLoopGroup，释放所有资源。
		}
	}
	
	public static void main(String[] args) throws Exception {
		int port = 9999;//设置端口值（抛出一个 NumberFormatException 如果该端口参数的格式不正确）
		if(args != null && args.length == 1){
			logger.info("Usage: "+EchoServer.class.getSimpleName()+" <port>");
			port = Integer.parseInt(args[0]);
			return;
		}
		new EchoServer(port).start();//呼叫服务器的 start() 方法
	}
}
