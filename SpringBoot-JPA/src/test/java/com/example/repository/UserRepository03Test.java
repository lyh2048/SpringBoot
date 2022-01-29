package com.example.repository;

import com.example.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@SpringBootTest
public class UserRepository03Test {
    @Autowired
    private UserRepository03 userRepository;

    @Test
    public void testFindByUsername() {
        User admin = userRepository.findByUsername("admin");
        System.out.println(admin);
    }

    @Test
    public void testFindByCreateTimeAfter() {
        // 创建分页插件
        PageRequest pageRequest = PageRequest.of(0, 2);
        // 执行分页操作
        LocalDateTime createTime = LocalDateTime.now().minusHours(10);
        Date date = Date.from(createTime.atZone(ZoneId.systemDefault()).toInstant());
        Page<User> userPage = userRepository.findByCreateTimeAfter(date, pageRequest);
        // 输出查询结果
        System.out.println(userPage.getContent());
        System.out.println(userPage.getTotalElements());
        System.out.println(userPage.getTotalPages());
    }
}
