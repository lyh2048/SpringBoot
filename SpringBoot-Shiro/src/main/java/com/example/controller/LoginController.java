package com.example.controller;

import com.example.exception.ForbiddenUserException;
import com.example.exception.IncorrectCaptchaException;
import com.example.shiro.ShiroUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class LoginController {
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        if (ShiroUtils.isAuthenticated()) {
            return "redirect:/";
        }
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request, Map<String, Object> map) {
        Object exception = request.getAttribute("shiroLoginFailure");
        String msg;
        if (exception != null) {
            if (exception instanceof UnknownAccountException) {
                msg = "用户名错误";
            } else if (exception instanceof IncorrectCredentialsException) {
                msg = "密码错误";
            } else if (exception instanceof IncorrectCaptchaException) {
                msg = "验证码错误";
            } else if (exception instanceof ForbiddenUserException) {
                msg = "该用户已被禁用，请联系管理员";
            } else {
                msg = "未知错误";
            }
            map.put("username", request.getParameter("username"));
            map.put("password", request.getParameter("password"));
            map.put("msg", msg);
            return "login";
        }
        return "index";
    }
}
