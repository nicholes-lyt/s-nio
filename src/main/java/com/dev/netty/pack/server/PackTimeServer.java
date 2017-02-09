package com.dev.netty.pack.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

/**
 * 
 * @ClassName: TimeServer   
 * @Description: 服务端
 * @author liyut
 * @version 1.0 
 * @date 2017年2月8日 上午9:57:12
 */
public class PackTimeServer {
	
	private static final Logger logger = Logger.getLogger(PackTimeServer.class);
	
	public void bind(int port)throws Exception{
		//定义线程组 NioEventLoopGroup处理I/O操作的多线程事件循环器
		// ‘group1’接收到连接，就会把连接信息注册到‘group2’上
		EventLoopGroup group1 = new NioEventLoopGroup();
		EventLoopGroup group2 = new NioEventLoopGroup();
		try {
			//配置服务器NIO线程组
			ServerBootstrap b = new ServerBootstrap();
			b.group(group1, group2)
			.channel(NioServerSocketChannel.class)
			.localAddress(new InetSocketAddress(port))//设置 socket 地址使用所选的端口
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new PackTimeServerHandler());
				}
			});
			//绑定端口，同步等待成功
			ChannelFuture f = b.bind(port).sync();
			logger.info("server name is "+PackTimeServer.class.getName() + " started and listen on " + f.channel().localAddress());
			//等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group1.shutdownGracefully();
			group2.shutdownGracefully();
		}
	}
	
	public static void main(String[] args)throws Exception {
		new PackTimeServer().bind(8010);
	}
	
}
