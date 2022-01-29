package com.example.repository;

import com.example.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class UserRepository04Test {
    @Autowired
    private UserRepository04 userRepository;

    @Test
    public void testFindByUsername01() {
        User admin = userRepository.findByUsername01("admin");
        System.out.println(admin);
    }
    @Test
    public void testFindByUsername02() {
        User admin = userRepository.findByUsername02("admin");
        System.out.println(admin);
    }

    @Test
    public void testFindByUsername03() {
        User admin = userRepository.findByUsername03("admin");
        System.out.println(admin);
    }

    /**
     * 更新操作，需要在事务中
     * 在单元测试中，事务默认回滚
     */
    @Test
    @Transactional
    public void tesUpdateUsernameById() {
        userRepository.updateUsernameById(8, "zs");
    }
}
