package com.mvc.springconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 
 * @ClassName: AppConfig   
 * @Description: 加载spirng配置文件
 * @author liyut
 * @version 1.0 
 * @date 2017年2月10日 下午3:27:22
 */
@Configuration
@EnableWebMvc
@ImportResource({"classpath*:/applicationContext.xml"})
public class AppConfig {
}
