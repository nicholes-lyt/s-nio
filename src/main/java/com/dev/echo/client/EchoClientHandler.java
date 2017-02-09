package com.dev.echo.client;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * 
 * @ClassName: EchoClientHandler
 * @Description: 客户端
 * @author liyut
 * @version 1.0
 * @date 2017年2月5日 下午2:15:17
 */
@Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private static final Logger logger = Logger.getLogger(EchoClientHandler.class);
	
	/**
	 * 
	 * 当被通知该 channel 是活动的时候就发送信息
	 * 覆盖方法channelActive详细说明 <br>
	 * @author liyut
	 * @date 2017年2月5日 下午2:20:02 </pre>
	 * @param 参数类型 参数名 说明
	 * @return 返回值类型 说明
	 * @throws 异常类型 说明
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
	 */
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(Unpooled.copiedBuffer("连接成功：Hello World Netty rocks!",
				CharsetUtil.UTF_8));
	}
	
	/**
	 * 
	 * 记录接收到的消息
	 * 覆盖方法channelRead0详细说明 <br>
	 * @author liyut
	 * @date 2017年2月5日 下午2:20:20 </pre>
	 * @param 参数类型 参数名 说明
	 * @return 返回值类型 说明
	 * @throws 异常类型 说明
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg)
			throws Exception {
		logger.info("Client received: " + msg.toString(CharsetUtil.UTF_8));
	}
	
	/**
	 * 记录日志错误并关闭 channel
	 * TODO 覆盖方法exceptionCaught简单说明 <br><pre>
	 * 覆盖方法exceptionCaught详细说明 <br>
	 * @author liyut
	 * @date 2017年2月5日 下午2:19:23 </pre>
	 * @param 参数类型 参数名 说明
	 * @return 返回值类型 说明
	 * @throws 异常类型 说明
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { 
		cause.printStackTrace();
		ctx.close();
	}

}
