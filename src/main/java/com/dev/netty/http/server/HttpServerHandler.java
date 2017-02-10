package com.dev.netty.http.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @ClassName: HttpServerHandler   
 * @Description: http --- 服务端处理
 * @author liyut
 * @version 1.0 
 * @date 2017年2月8日 上午11:50:56
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter{
	
	private static final Logger logger = Logger.getLogger(HttpServerHandler.class);
	
	private FullHttpRequest request;
	
	private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
    private static final AsciiString CONNECTION = new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");
	
	/**
	 * 
	 * 读取数据
	 * 覆盖方法channelRead详细说明 <br>
	 * @author liyut
	 * @date 2017年2月8日 上午11:52:27 </pre>
	 * @param 参数类型 参数名 说明
	 * @return 返回值类型 说明
	 * @throws 异常类型 说明
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception {
		if(msg instanceof FullHttpRequest){
			request = (FullHttpRequest)msg;
			String uri = request.uri();
			logger.info("requersURI:"+uri);
			//去除浏览器"/favicon.ico"的干扰
			if(uri.equals("/favicon.ico")){
				return;
			}
			//新建一个返回消息的Json对象
			JSONObject responseJson = new JSONObject();
			//把客户端的请求数据格式化为Json对象
			@SuppressWarnings("unused")
			JSONObject requestJson = new JSONObject();
			
			try {
				requestJson = JSONObject.parseObject(parseJosnRequest(request));
			} catch (Exception e) {
				ResponseJson(ctx, request, new String("error json"));
				return;
			}
			String interfacePath = "/soa/service";
			//根据不同的请求API做不同的处理(路由分发)，只处理POST方法
			if(request.method() == HttpMethod.POST){
				if(uri.equals(interfacePath)){
					responseJson.put("data", "POST接口请求成功！");
				}
			}else if(request.method() == HttpMethod.GET){
				if(uri.equals(interfacePath)){
					responseJson.put("data", "GET接口请求成功！");
				}
			}else{
				//错误处理
                responseJson.put("error", "404 Not Find");
			}
			
			//向客户端发送结果
			ResponseJson(ctx, request, responseJson.toString());
			
		}
	}
	
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();//刷新后才将数据发出到SocketChannel
    }
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
		logger.error(cause.getMessage());
		ctx.close();
	}
	
	/**
	 * 
	 * @Description: 响应HTTP请求
	 * @author liyut
	 * @version 1.0 
	 * @date 2017年2月9日 上午10:11:37 
	 * @param ctx
	 * @param req
	 * @param jsonStr 
	 * @return void
	 * @throws Exception 
	 */
    private void ResponseJson(ChannelHandlerContext ctx, FullHttpRequest req ,String jsonStr) throws Exception{
    	//判断是否长连接
        boolean keepAlive = HttpUtil.isKeepAlive(req);
        byte[] jsonByteByte = jsonStr.getBytes("UTF-8");
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(jsonByteByte));
        response.headers().set(CONTENT_TYPE, "text/json;charset=UTF-8");
        response.headers().set("Accept-Charset", "UTF-8");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        if (!keepAlive) {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.writeAndFlush(response);
        }
    }
    
    /**
     * 
     * @Description: 获取请求的内容
     * @author liyut
     * @version 1.0 
     * @date 2017年2月9日 上午10:13:56 
     * @param request
     * @return 
     * @return String
     */
    private String parseJosnRequest(FullHttpRequest request) {
    	ByteBuf jsonBuf = request.content();
        String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
        return jsonStr;
    }
}
