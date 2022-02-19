package com.example.demo.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import xyz.liuyuhe.starter.service.HelloService;

import javax.annotation.Resource;

@Service
public class TestController implements CommandLineRunner {

    @Resource
    private HelloService helloService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(helloService.toString());
    }
}
