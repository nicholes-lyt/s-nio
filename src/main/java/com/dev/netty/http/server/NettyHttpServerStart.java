package com.dev.netty.http.server;

import org.apache.log4j.Logger;



/**
 * 
 * @ClassName: NettyHttpServerStart   
 * @Description: 启动netty服务
 * @author liyut
 * @version 1.0 
 * @date 2017年2月16日 上午11:06:58
 */
public class NettyHttpServerStart {
	
	private static final Logger logger = Logger.getLogger(NettyHttpServerStart.class);
	
	public static void main(String[] args) {
		try {
			new HttpServer().start();
		} catch (Exception e) {
			logger.error("启动服务失败……", e);
		}
	}
}
