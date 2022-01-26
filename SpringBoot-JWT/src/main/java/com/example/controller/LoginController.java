package com.example.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.entity.User;
import com.example.service.UserService;
import com.example.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class LoginController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        log.info("用户名: [{}]", user.getUsername());
        log.info("密码: [{}]", user.getPassword());
        Map<String, Object> result = new HashMap<>();
        try {
            User login = userService.login(user.getUsername(), user.getPassword());
            log.info(login.toString());
            // 生成token
            Map<String, String> payload = new HashMap<>();
            payload.put("id", login.getId().toString());
            payload.put("username", login.getUsername());
            String token = JwtUtils.getToken(payload);
            result.put("token", token);
            result.put("state", true);
            result.put("msg", "登录成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            result.put("state", false);
            result.put("msg", "登录失败");
        }
        return result;
    }

    @GetMapping("/test")
    public Map<String, Object> test(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        // 从request中获取token
        String token = request.getHeader("token");
        DecodedJWT verify = JwtUtils.verify(token);
        String userId = verify.getClaim("id").asString();
        String username = verify.getClaim("username").asString();
        log.info("用户id: [{}]", userId);
        log.info("用户名: [{}]", username);
        result.put("userId", userId);
        result.put("username", username);
        result.put("state", true);
        result.put("msg", "成功");
        return result;
    }
}
