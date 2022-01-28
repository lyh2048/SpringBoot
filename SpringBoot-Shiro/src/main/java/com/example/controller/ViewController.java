package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {
    /**
     * 欢迎页面
     */
    @RequestMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    /**
     * 主页
     */
    @RequestMapping({"/", "/index"})
    public String index() {
        return "index";
    }
}
