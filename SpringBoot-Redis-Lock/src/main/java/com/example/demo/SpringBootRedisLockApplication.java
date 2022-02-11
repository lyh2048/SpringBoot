package com.example.demo;

import com.example.demo.interceptor.CacheKeyGenerator;
import com.example.demo.interceptor.LockKeyGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootRedisLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRedisLockApplication.class, args);
    }

    @Bean
    public CacheKeyGenerator cacheKeyGenerator() {
        return new LockKeyGenerator();
    }
}
