package com.dev.echo.server;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.CharsetUtil;

/**
 * 
 * @ClassName: EchoServerHandler
 * @Description: 通过 ChannelHandler 来实现服务器的逻辑
 * @author liyut
 * @version 1.0
 * @date 2017年2月5日 上午9:59:09
 */
@Sharable
// 注解@Sharable可以让它在channels间共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
	
	private static final Logger logger = Logger.getLogger(EchoServerHandler.class);
	
	/**
	 * 
	 * 读取数据 <br><pre>
	 * 覆盖方法channelRead详细说明 <br>
	 * @author liyut
	 * @date 2017年2月5日 上午10:59:26 </pre>
	 * @param 参数类型 参数名 说明
	 * @return 返回值类型 说明
	 * @throws 异常类型 说明
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf in = (ByteBuf)msg;
		logger.info("Server received: " + in.toString(CharsetUtil.UTF_8));
		ctx.write(in);
	}
	
	/**
	 * 
	 * 读取数据完成 <br><pre>
	 * 覆盖方法channelReadComplete详细说明 <br>
	 * @author liyut
	 * @date 2017年2月5日 上午11:01:02 </pre>
	 * @param 参数类型 参数名 说明
	 * @return 返回值类型 说明
	 * @throws 异常类型 说明
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
	 */
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}
	
	/**
	 * 
	 * 捕获异常 <br><pre>
	 * 覆盖方法exceptionCaught详细说明 <br>
	 * @author liyut
	 * @date 2017年2月5日 上午11:01:05 </pre>
	 * @param 参数类型 参数名 说明
	 * @return 返回值类型 说明
	 * @throws 异常类型 说明
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
		//出现异常关闭连接
		cause.printStackTrace();
		ctx.close();
	}

}
