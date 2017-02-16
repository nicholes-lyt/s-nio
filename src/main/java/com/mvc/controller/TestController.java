package com.mvc.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hello")
public class TestController {

    @RequestMapping(value="/netty.htm")
    public void getShopInJSON(HttpServletRequest request,HttpServletResponse resp) {
    	resp.setCharacterEncoding("UTF-8");
        try {
			resp.getWriter().write("访问netty-httpServer服务器！");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}
