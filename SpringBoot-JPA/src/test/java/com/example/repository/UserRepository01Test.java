package com.example.repository;

import com.example.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class UserRepository01Test {
    @Autowired
    private UserRepository01 userRepository;

    /**
     * 插入一条记录
     */
    @Test
    public void testSave() {
        User user = new User().setUsername(UUID.randomUUID().toString())
                .setPassword(DigestUtils.md5DigestAsHex("123456".getBytes())).setCreateTime(new Date());
        userRepository.save(user);
    }

    /**
     * 更新一条记录
     */
    @Test
    public void testUpdate() {
        // 先查一条记录
        Optional<User> user = userRepository.findById(4);
        Assert.isTrue(user.isPresent(), "记录不能为空");
        // 更新
        User updateUser = user.get();
        updateUser.setPassword(DigestUtils.md5DigestAsHex("admin".getBytes()));
        userRepository.save(updateUser);
    }

    /**
     * 根据ID，删除一条数据
     */
    @Test
    public void testDelete() {
        userRepository.deleteById(4);
    }

    /**
     * 根据ID，查询一条记录
     */
    @Test
    public void testSelectById() {
        Optional<User> user = userRepository.findById(7);
        Assert.isTrue(user.isPresent(), "记录为空");
        System.out.println(user.get());
    }

    /**
     * 根据ID数组，查询多条记录
     */
    @Test
    public void testSelectByIds() {
        Iterable<User> users = userRepository.findAllById(Arrays.asList(4, 6, 7));
        users.forEach(System.out::println);
    }
}