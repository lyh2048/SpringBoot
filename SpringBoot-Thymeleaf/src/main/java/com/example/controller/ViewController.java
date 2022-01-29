package com.example.controller;

import com.example.vo.Author;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class ViewController {
    @GetMapping("/index")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView();
        // 设置跳转的视图
        view.setViewName("index");
        // 设置属性
        view.addObject("title", "主页");
        view.addObject("description", "欢迎来到我的主页");
        Author author = new Author().setName("刘青松").setPhone("15966154189").setEmail("qs@qq.com");
        log.info(author.toString());
        view.addObject("author", author);
        return view;
    }

    /**
     * 与上面的写法不同，但是结果一致
     */
    @GetMapping("/")
    public String root(HttpServletRequest request) {
        // 设置属性
        request.setAttribute("title", "主页");
        request.setAttribute("description", "欢迎来到我的主页");
        Author author = new Author().setName("刘青松").setPhone("15966154189").setEmail("qs@qq.com");
        log.info(author.toString());
        request.setAttribute("author", author);
        return "index";
    }
}
