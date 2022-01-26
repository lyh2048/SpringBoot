package com.example.service.impl;

import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;


    @Override
    public User login(String username, String password) {
        // 根据用户名和密码查询数据库
        User login = userMapper.login(username, DigestUtils.md5DigestAsHex(password.getBytes()));
        if (login != null) {
            return login;
        }
        throw new RuntimeException("登录失败，用户名或密码错误");
    }
}
