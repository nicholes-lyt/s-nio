package com.dev.netty.base.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 
 * @ClassName: TimeClient   
 * @Description: Netty时间服务器---客户端
 * @author liyut
 * @version 1.0 
 * @date 2017年2月7日 下午2:23:33
 */
public class TimeClient {
	
	/**
	 * 
	 * @Description: 建立连接
	 * @author liyut
	 * @version 1.0 
	 * @date 2017年2月7日 下午2:24:29 
	 * @param port
	 * @param host
	 * @throws Exception 
	 * @return void
	 */
	public void connect(int port,String host)throws Exception{
		//配置客户端NIO线程组
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new TimeClientHandler());
				}
			});
			//发起异步连接操作
			ChannelFuture f = b.connect(host,port).sync();
			//等待客户端链路关闭
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//退出，释放线程组
			group.shutdownGracefully().sync();
		}
	}
	
	public static void main(String[] args) throws Exception {
		int port = 8080;
		String host = "127.0.0.1";
		if(args != null && args.length == 2){
			port = Integer.parseInt(args[0]);
			host = args[1];
		}
		new TimeClient().connect(port, host);
	}
	
}
