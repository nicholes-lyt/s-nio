package com.dev.netty.http.server;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.CharsetUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

/**
 * 
 * @ClassName: ServletHandler   
 * @Description: 处理servlet请求
 * @author liyut
 * @version 1.0 
 * @date 2017年2月10日 下午3:49:24
 */
public class ServletHandler extends SimpleChannelInboundHandler<FullHttpRequest>{
	
	private static final Logger logger = Logger.getLogger(ServletHandler.class);
	
	private final Servlet servlet;
	
	private final ServletContext servletContext;
	
	public ServletHandler(Servlet servlet){
		this.servlet = servlet;
		this.servletContext = servlet.getServletConfig().getServletContext();
	}
	
	/**
	 * 
	 * @Description: MockHttpServletRequest创建http请求信息
	 * @author liyut
	 * @version 1.0 
	 * @date 2017年2月10日 下午4:05:49 
	 * @param fullHttpRequest
	 * @return 
	 * @return MockHttpServletRequest
	 */
	private MockHttpServletRequest createServletRequest(FullHttpRequest fullHttpRequest){
		//uri内容
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(fullHttpRequest.uri()).build();
		MockHttpServletRequest servletRequest = new MockHttpServletRequest(this.servletContext);
		servletRequest.setRequestURI(uriComponents.getPath());
		servletRequest.setPathInfo(uriComponents.getPath());
		servletRequest.setMethod(fullHttpRequest.method().name());
		if (uriComponents.getScheme() != null) {
			servletRequest.setScheme(uriComponents.getScheme());
		}
		if (uriComponents.getHost() != null) {
			servletRequest.setServerName(uriComponents.getHost());
		}
		if (uriComponents.getPort() != -1) {
			servletRequest.setServerPort(uriComponents.getPort());
		}
		//设置头信息
		for (String name : fullHttpRequest.headers().names()) {
			servletRequest.addHeader(name, fullHttpRequest.headers().get(name));
		}
		// 将post请求的参数，添加到HttpServletRrequest的parameter
        try {
            ByteBuf buf=fullHttpRequest.content();
            int readable=buf.readableBytes();
            byte[] bytes=new byte[readable];
            buf.readBytes(bytes);
            String contentStr = UriUtils.decode(new String(bytes,"UTF-8"), "UTF-8");
            for(String params : contentStr.split("&")){
                String[] para = params.split("=");
                if(para.length > 1){
                    servletRequest.addParameter(para[0],para[1]);
                } else {
                    servletRequest.addParameter(para[0],"");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        try {
			if (uriComponents.getQuery() != null) {
				String query = UriUtils.decode(uriComponents.getQuery(), "UTF-8");
				servletRequest.setQueryString(query);
			}
			for (Entry<String, List<String>> entry : uriComponents.getQueryParams().entrySet()) {
				for (String value : entry.getValue()) {
					servletRequest.addParameter(
							UriUtils.decode(entry.getKey(), "UTF-8"),
							UriUtils.decode(value, "UTF-8"));
				}
			}
		}
		catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
        
        return servletRequest;
	}
	
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest)
			throws Exception {
		if (!fullHttpRequest.getDecoderResult().isSuccess()) {
			sendError(ctx, BAD_REQUEST);
			return;
		}
		//http请求和响应
		MockHttpServletRequest servletRequest = createServletRequest(fullHttpRequest);
		MockHttpServletResponse servletResponse = new MockHttpServletResponse();
		String uri = servletRequest.getRequestURI();
		logger.info("requersURI:"+uri);
		logger.info("request请求参数: "+servletRequest.getQueryString());
		//去除浏览器"/favicon.ico"的干扰
		if(uri.equals("/favicon.ico")){
			return;
		}
		//启动访问显示
		if(uri.length() == 1){
			String result = "欢迎访问Netty-HttpServer服务器";
			servletResponse.setCharacterEncoding("UTF-8");
			servletResponse.getWriter().write(result);
		}
		servletResponse.addHeader(CONTENT_TYPE, "text/json;charset=UTF-8");
		this.servlet.service(servletRequest, servletResponse);
		HttpResponseStatus status = HttpResponseStatus.valueOf(servletResponse.getStatus());
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);

		for (String name : servletResponse.getHeaderNames()) {
			for (Object value : servletResponse.getHeaderValues(name)) {
				logger.info(" name:"+name+" value:"+value);
				response.headers().add(name, value);
			}
		}

		ctx.writeAndFlush(response);
		InputStream contentStream = new ByteArrayInputStream(servletResponse.getContentAsByteArray());
		ChannelFuture writeFuture = ctx.writeAndFlush(new ChunkedStream(contentStream));
		writeFuture.addListener(ChannelFutureListener.CLOSE);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		if (ctx.channel().isActive()) {
			sendError(ctx, INTERNAL_SERVER_ERROR);
		}
	}
	
	@SuppressWarnings("deprecation")
	private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		ByteBuf content = Unpooled.copiedBuffer(
				"Failure: " + status.toString() + "\r\n",
				CharsetUtil.UTF_8);
		FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
				HTTP_1_1,
				status,
				content
		);
		fullHttpResponse.headers().add(CONTENT_TYPE, "text/plain; charset=UTF-8");
		// Close the connection as soon as the error message is sent.
		ctx.write(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
	}
}
