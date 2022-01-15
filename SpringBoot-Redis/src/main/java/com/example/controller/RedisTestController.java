package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redisTest")
public class RedisTestController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping
    public String testRedis() {
        // 设置值
        redisTemplate.opsForValue().set("name", "lyh");
        // 获取值
        return (String) redisTemplate.opsForValue().get("name");
    }
}
