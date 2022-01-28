package com.example.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 扩展登录验证字段
 */
public class CaptchaUsernamePasswordToken extends UsernamePasswordToken {
    // 验证码
    private String captcha;

    public CaptchaUsernamePasswordToken(String username, char[] password,
                                        boolean rememberMe, String host, String captcha) {
        super(username, password, rememberMe, host);
        this.captcha = captcha;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
