package com.dev.netty.pack.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 
 * @ClassName: TimeServerHandler   
 * @Description: Netty - 粘包服务端
 * @author liyut
 * @version 1.0 
 * @date 2017年2月8日 上午9:55:16
 */
public class PackTimeServerHandler extends ChannelInboundHandlerAdapter{
	
	private static final Logger logger = Logger.getLogger(PackTimeServerHandler.class);
	
	private int counter;
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf in = (ByteBuf)msg;
		byte[] req = new byte[in.readableBytes()];
	    in.readBytes(req);
	    String body = new String(req, "UTF-8").substring(0, req.length - System.getProperty("line.separator").length());
	    logger.info("Server received: " + body + " counter------------>"+ ++counter);
	    String time = "Server now time".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "no order";
		time += System.getProperty("line.separator");
	    ByteBuf resp = Unpooled.copiedBuffer(time.getBytes());
	    ctx.write(resp);
    }
	
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();//刷新后才将数据发出到SocketChannel
    }
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
		//捕获异常，关闭连接
        ctx.close();
    }
	
}
