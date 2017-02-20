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
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.CharsetUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


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

	public ServletHandler(Servlet servlet) {
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
	private MockHttpServletRequest createServletRequest(
			FullHttpRequest fullHttpRequest) throws Exception{
		// uri内容
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(
				fullHttpRequest.uri()).build();
		MockHttpServletRequest servletRequest = new MockHttpServletRequest(
				this.servletContext);
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
		// 设置头信息
		for (String name : fullHttpRequest.headers().names()) {
			servletRequest.addHeader(name, fullHttpRequest.headers().get(name));
		}
		
		// 将请求的参数添加到HttpServletRrequest的parameter
		Map<String, String> params = parseParams(fullHttpRequest);
		servletRequest.addParameters(params);

		return servletRequest;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void channelRead0(ChannelHandlerContext ctx,
			FullHttpRequest fullHttpRequest) throws Exception {
		if (!fullHttpRequest.getDecoderResult().isSuccess()) {
			sendError(ctx, BAD_REQUEST);
			return;
		}
		// http请求和响应
		MockHttpServletRequest servletRequest = createServletRequest(fullHttpRequest);
		MockHttpServletResponse servletResponse = new MockHttpServletResponse();
		String uri = servletRequest.getRequestURI();
		logger.info("requersURI:	" + uri);
		printParams(servletRequest);

		// 中文乱码处理
		servletResponse.setCharacterEncoding("UTF-8");
		servletResponse.addHeader(CONTENT_TYPE, "text/json;charset=UTF-8");

		// 去除浏览器"/favicon.ico"的干扰
		if (uri.equals("/favicon.ico")) {
			return;
		}
		// 启动访问显示
		if (uri.length() == 1) {
			String result = "欢迎访问Netty-HttpServer服务器";
			servletResponse.getWriter().write(result);
		}

		this.servlet.service(servletRequest, servletResponse);
		HttpResponseStatus status = HttpResponseStatus.valueOf(servletResponse
				.getStatus());
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);

		if (servletResponse != null && servletResponse.getHeaderNames() != null) {
			for (String name : servletResponse.getHeaderNames()) {
				for (Object value : servletResponse.getHeaderValues(name)) {
					logger.info("	name:" + name + "	value:" + value);
					response.headers().add(name, value);
				}
			}
		}

		ctx.writeAndFlush(response);
		InputStream contentStream = new ByteArrayInputStream(
				servletResponse.getContentAsByteArray());
		ChannelFuture writeFuture = ctx.writeAndFlush(new ChunkedStream(
				contentStream));
		writeFuture.addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		if (ctx.channel().isActive()) {
			sendError(ctx, INTERNAL_SERVER_ERROR);
		}
	}

	@SuppressWarnings("deprecation")
	private static void sendError(ChannelHandlerContext ctx,
			HttpResponseStatus status) {
		ByteBuf content = Unpooled.copiedBuffer("Failure: " + status.toString()
				+ "\r\n", CharsetUtil.UTF_8);
		FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
				HTTP_1_1, status, content);
		fullHttpResponse.headers().add(CONTENT_TYPE,
				"text/plain; charset=UTF-8");
		// Close the connection as soon as the error message is sent.
		ctx.write(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
	}
	
	/**
	 * 
	 * @Description: 输出参数
	 * @author liyut
	 * @version 1.0 
	 * @date 2017年2月20日 下午12:50:31 
	 * @param servletRequest 
	 * @return void
	 */
	private void printParams(MockHttpServletRequest servletRequest){
		Enumeration<String> parameterNames = servletRequest.getParameterNames();
		while (parameterNames.hasMoreElements()) {
		    String key = (String) parameterNames.nextElement();
		    String val = servletRequest.getParameter(key);
		    logger.info("http请求参数：Key="+key+" Value="+val+"");
		}
	}
	
	/**
	 * 
	 * @Description: FullHttpRequest处理请求参数
	 * @author liyut
	 * @version 1.0 
	 * @date 2017年2月20日 上午11:57:27 
	 * @param fullHttpRequest
	 * @return
	 * @throws Exception 
	 * @return Map<String,String>
	 */
	private Map<String, String> parseParams(FullHttpRequest fullHttpRequest) throws Exception {
        HttpMethod method = fullHttpRequest.method();
        Map<String, String> parmMap = new HashMap<String, String>();
        try {
        	if (HttpMethod.GET == method) {
                // 是GET请求
                QueryStringDecoder decoder = new QueryStringDecoder(fullHttpRequest.uri());
                for(Map.Entry<String, List<String>> entry : decoder.parameters().entrySet()){
                	// entry.getValue()是一个List, 只取第一个元素
                	parmMap.put(entry.getKey(), entry.getValue().get(0));
                }
            } else if (HttpMethod.POST == method) {
                // 是POST请求
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fullHttpRequest);
                decoder.offer(fullHttpRequest);

                List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
                for (InterfaceHttpData parm : parmList) {
                	Attribute data = (Attribute)parm;
                    parmMap.put(data.getName(), data.getValue());
                }
            } else {
                // 不支持其它方法
                logger.error("只支持POST、GET请求！");
            }
		} catch (Exception e) {
			logger.error("只支持POST、GET请求！",e);
		}
        return parmMap;
    }
}
