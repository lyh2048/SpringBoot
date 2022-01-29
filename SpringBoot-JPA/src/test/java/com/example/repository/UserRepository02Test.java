package com.example.repository;

import com.example.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
public class UserRepository02Test {
    @Autowired
    private UserRepository02 userRepository;

    /**
     * 排序
     */
    @Test
    public void testFindAll() {
        // 创建排序条件
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // 执行排序操作
        Iterable<User> userIterable = userRepository.findAll(sort);
        // 输出
        userIterable.forEach(System.out::println);
    }

    /**
     * 分页
     */
    @Test
    public void testFindPage() {
        // 创建排序条件
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // 创建分页条件
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        // 执行分页操作
        Page<User> userPage = userRepository.findAll(pageRequest);
        // 输出结果
        System.out.println(userPage.getContent());
        System.out.println(userPage.getTotalElements());
        System.out.println(userPage.getTotalPages());
    }
}
