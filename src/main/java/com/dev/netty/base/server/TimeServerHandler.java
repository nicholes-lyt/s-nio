package com.dev.netty.base.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

import org.apache.log4j.Logger;

import com.dev.utils.DateUtil;

/**
 * 
 * @ClassName: TimeServerHandler   
 * @Description: TODO(描述该类做什么)
 * @author liyut
 * @version 1.0 
 * @date 2017年2月7日 下午2:03:09
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter{
	
	 private static final Logger logger = Logger.getLogger(TimeServerHandler.class);
	 
	 private int counter;
	 
	 public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		 //字节流操作
		 ByteBuf buf = (ByteBuf)msg;
		 byte[] req = new byte[buf.readableBytes()];
		 buf.readBytes(req);
		 String body = new String(req,"UTF-8");
		 logger.info("时间服务器收到命令："+body+" counter------------>"+ ++counter);
		 //判断消息
		 String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? DateUtil.parseToString(new Date(System.currentTimeMillis()), DateUtil.CN_yyyyMMddHHmmss) : "BAD ORDER";
		 logger.info("服务端时间："+currentTime);
		 ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		 ctx.write(resp);
	 }
	 /**
	  * 
	  * 读取客户端的数据完成
	  * 覆盖方法channelReadComplete详细说明 <br>
	  * @author liyut
	  * @date 2017年2月7日 下午3:41:19 </pre>
	  * @param 参数类型 参数名 说明
	  * @return 返回值类型 说明
	  * @throws 异常类型 说明
	  * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
	  */
	 public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	 }
	 
	 public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	            throws Exception {
		 ctx.close();
	 }
}
