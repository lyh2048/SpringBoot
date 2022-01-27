package com.example.controller;

import com.example.entity.User;
import com.example.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
@Api(value = "AuthController", tags = "登录/注册")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * 登录
     */
    @PostMapping("/login")
    @ApiOperation(value = "登录")
    public String login(@RequestBody User user) {
        return authService.login(user.getUsername(), user.getPassword());
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    @ApiOperation(value = "注册")
    public String register(@RequestBody User user) {
        return authService.register(user);
    }
}
