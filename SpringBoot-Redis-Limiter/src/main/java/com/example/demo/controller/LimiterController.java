package com.example.demo.controller;

import com.example.demo.annotation.Limit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class LimiterController {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

    @Limit(key = "test", period = 100, count = 10)
    @GetMapping("/test")
    public int testLimiter() {
        // 该接口100s内最多允许访问10次
        return ATOMIC_INTEGER.incrementAndGet();
    }
}
