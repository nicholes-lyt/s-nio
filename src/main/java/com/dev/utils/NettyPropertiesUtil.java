package com.dev.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * @ClassName: PropertiesUtil
 * @Description: 读取properties文件
 * @author liyut
 * @version 1.0
 * @date 2017年2月15日 上午11:37:31
 */
public class NettyPropertiesUtil {

	private static final Logger logger = Logger.getLogger(NettyPropertiesUtil.class);
	// netty配置文件
	public static final String PROFILE = "nettyserver.properties";
	// ssl认证端口
	public static final String SSLPORT_KEY = "netty.httpserver.ssl.port";
	// http端口
	public static final String HTTPPORT_KEY = "netty.httpserver.port";
	// 已完成三次握手的请求的队列的最大长度
	public static String BACKLOG_KEY = "netty.sobacklog";
	// 长连接
	public static final String KEEPALIVE_KEY = "netty.sokeepalive";
	// spring 配置文件
	public static final String SPRINGCONFIG_KEY = "spring.config.file";

	public static String getValue(String key, String proFile) {
		String value = "";
		try {
			// 加载src下的文件
			InputStream inputStream = Object.class.getResourceAsStream("/" + proFile);
			Properties properties = new Properties();
			properties.load(inputStream);
			value = properties.getProperty(key);
		} catch (Exception e) {
			logger.error("读取nettyserver.properties文件错误", e);
		}
		return value;
	}
	
	public static String getSpringConfig(){
		String config = "";
		try {
			config = getValue(NettyPropertiesUtil.SPRINGCONFIG_KEY, NettyPropertiesUtil.PROFILE);
		} catch (Exception e) {
			logger.error("读取spring配置文件错误", e);
		}
		return config;
	}


}
