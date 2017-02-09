package com.dev.netty.unpack.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;

/**
 * 
 * @ClassName: TimeClient   
 * @Description: Netty时间服务器---客户端
 * @author liyut
 * @version 1.0 
 * @date 2017年2月7日 下午2:23:33
 */
public class UnPackTimeClient {
	
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
			.remoteAddress(new InetSocketAddress(host, port))//设置服务器的 InetSocketAddress
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					//解码器：解决粘包、拆包
					ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new UnPackTimeClientHandler());
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
		int port = 8011;
		String host = "127.0.0.1";
		if(args != null && args.length == 2){
			port = Integer.parseInt(args[0]);
			host = args[1];
		}
		new UnPackTimeClient().connect(port, host);
	}
	
}
