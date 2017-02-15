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
public class PropertiesUtil {
	
	private static final Logger logger = Logger.getLogger(PropertiesUtil.class);
	
	public static String getValue(String key,String proFile){
		String value = "";
		try {
			//加载src下的文件
			InputStream inputStream = Object.class.getResourceAsStream("/"+proFile);
			Properties properties = new Properties();
			properties.load(inputStream);
			value = properties.getProperty(key);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return value;
	}
	
	public static void main(String[] args) {
		System.out.println(getValue("netty.httpserver.port","nettyserver.properties"));
	}
	
}
