package com.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;

@Controller
public class ServiceInterfaceController {
	
	/**
	 * 
	 * @Description: 服务入口
	 * @author liyut
	 * @version 1.0 
	 * @date 2017年2月10日 下午5:06:38 
	 * @param scode
	 * @param req
	 * @param resp
	 * @throws Exception 
	 * @return void
	 */
	@RequestMapping(method=RequestMethod.POST,value="/soa/service/{scode}.htm",produces = "text/html; charset=UTF-8")
	public void service(@PathVariable String scode,HttpServletRequest req,HttpServletResponse resp) throws Exception{
		//resp.setHeader(CONTENT_TYPE, "text/json;charset=UTF-8");
		JSONObject json = new JSONObject();
		json.put("data", "编号【"+scode+"】接口访问成功！");
		resp.getWriter().write(json.toString());
	}
	
}
