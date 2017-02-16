package com.dev.netty.http.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.servlet.ServletException;

import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.dev.utils.NettyPropertiesUtil;



/**
 * 
 * @ClassName: ServletChannelInitializer
 * @Description: 处理servlet通道
 * @author liyut
 * @version 1.0
 * @date 2017年2月10日 下午4:11:11
 */
public class ServletChannelInitializer extends
		ChannelInitializer<SocketChannel> {

	private final DispatcherServlet dispatcherServlet;
	
	//SSL认证
	private SslContext sslContext;
	



	/**
	 * 获取spring配置文件信息
	 * 
	 * @param @throws ServletException
	 * @version 1.0
	 * @date 2017年2月10日 下午4:12:31
	 */
	public ServletChannelInitializer(SslContext sslContext) throws ServletException {
		
		this.sslContext = sslContext;

		MockServletContext servletContext = new MockServletContext();
		MockServletConfig servletConfig = new MockServletConfig(servletContext);

		/*AnnotationConfigWebApplicationContext wac = new AnnotationConfigWebApplicationContext();
		wac.setServletContext(servletContext);
		wac.setServletConfig(servletConfig);
		wac.register(AppConfig.class);
		wac.refresh();

		this.dispatcherServlet = new DispatcherServlet(wac);
		this.dispatcherServlet.init(servletConfig);*/

		// set spring config in xml
		this.dispatcherServlet = new DispatcherServlet();
		this.dispatcherServlet.setContextConfigLocation("classpath*:/"+NettyPropertiesUtil.getValue(NettyPropertiesUtil.SPRINGCONFIG_KEY, NettyPropertiesUtil.PROFILE));
		this.dispatcherServlet.init(servletConfig);
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {

		ChannelPipeline p = ch.pipeline();
		// Uncomment the following line if you want HTTPS
//		 SSLEngine engine = 
//		 SecureChatSslContextFactory.getServerContext().createSSLEngine();
//		 engine.setUseClientMode(false);
		//p.addLast("ssl", new SslHandler(engine));
		//ssl判断
		if(sslContext != null){
			p.addLast(sslContext.newHandler(ch.alloc()));
		}
		p.addLast(new HttpServerCodec());
		p.addLast(new HttpObjectAggregator(2048));// HTTP 消息的合并处理
		// 用netty自带包解决粘包，拆包
		p.addLast(new LineBasedFrameDecoder(1024));
		p.addLast(new ChunkedWriteHandler());
		p.addLast(new ServletHandler(this.dispatcherServlet));
	}

}
