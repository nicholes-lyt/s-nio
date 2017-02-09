package com.dev.netty.base.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.log4j.Logger;

/**
 * 
 * @ClassName: TimeClientHandler   
 * @Description: Netty时间客户端
 * @author liyut
 * @version 1.0 
 * @date 2017年2月7日 下午2:39:21
 */
public class TimeClientHandler extends SimpleChannelInboundHandler<ByteBuf>{
	
	private static final Logger logger = Logger.getLogger(TimeClientHandler.class);
	
	private int counter;
	
	private byte[] reqOrder;
	
	public TimeClientHandler(){
		reqOrder = "QUERY TIME ORDER".getBytes();
	}
	
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("客户端与服务器连接成功……");
		for(int i = 0; i < 1; i++){
			ByteBuf msg = null;
			msg = Unpooled.buffer(reqOrder.length);
			msg.writeBytes(reqOrder);
			ctx.writeAndFlush(msg);
		}
	}
	
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg)
			throws Exception {
		ByteBuf buf = msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req,"UTF-8");
		logger.info("客户端收到服务器时间为："+body+" counter------------>"+ ++counter);
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
		ctx.close();
	}
	
	
}
