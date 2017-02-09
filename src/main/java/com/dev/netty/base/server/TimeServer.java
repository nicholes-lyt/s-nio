package com.dev.netty.base.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.apache.log4j.Logger;

/**
 * 
 * @ClassName: TimeServer   
 * @Description: Netty时间服务器---服务端
 * @author liyut
 * @version 1.0 
 * @date 2017年2月7日 下午1:43:55
 */
public class TimeServer {
	private static final Logger logger = Logger.getLogger(TimeServer.class);
	public void bind(int port)throws Exception{
		//配置服务器端的NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup wokerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, wokerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)//临时存放已完成三次握手的请求的队列的最大长度
			//.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)//连接超时
			.childHandler(new ChildChannelHandler());
			//绑定端口，同步等待成功
			ChannelFuture f = b.bind(port).sync();
			logger.info("server name is "+TimeServer.class.getName() + " started and listen on " + f.channel().localAddress());
			//等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//退出，释放线程资源
			bossGroup.shutdownGracefully().sync();
			wokerGroup.shutdownGracefully().sync();
		}
	}
	
	/**
	 * 
	 * @ClassName: ChildChannelHandler   
	 * @Description: 自定义通道
	 * @author liyut
	 * @version 1.0 
	 * @date 2017年2月7日 下午1:54:09
	 */
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new TimeServerHandler());
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		int port = 8080;
		if(args != null && args.length == 1){
			try {
				port = Integer.parseInt(args[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		new TimeServer().bind(port);
	}
	
}
