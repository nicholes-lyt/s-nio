package com.dev.netty.pack.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.log4j.Logger;

/**
 * 
 * @ClassName: TimeClientHandler   
 * @Description: Netty - 粘包客户端
 * @author liyut
 * @version 1.0 
 * @date 2017年2月8日 上午10:18:02
 */
public class PackTimeClientHandler extends ChannelInboundHandlerAdapter{
	
	private static final Logger logger = Logger.getLogger(PackTimeClientHandler.class);
	
	private int counter;
	
	private byte[] reqOrder;
	
	public PackTimeClientHandler(){
		reqOrder = ("Server now time"+System.getProperty("line.separator")).getBytes();
	}
	
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("客户端与服务器连接成功……");
		for(int i = 0; i < 10; i++){
			ByteBuf msg = null;
			msg = Unpooled.buffer(reqOrder.length);
			msg.writeBytes(reqOrder);
			ctx.writeAndFlush(msg);
		}
	}
	
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf buf = (ByteBuf)msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req,"UTF-8");
		logger.info("Client received time："+body+" counter------------>"+ ++counter);
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
		ctx.close();
	}
	
	
}
