package com.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/hello")
public class TestController {

    @RequestMapping(value="/netty.htm", produces = "text/html; charset=utf-8")
    public @ResponseBody String getShopInJSON(HttpServletRequest request) {
        return "访问netty-httpServer服务器！";
    }

}
