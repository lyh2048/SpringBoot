package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

public class JwtUtils {
    public static final String SIGN = "secret";

    /**
     * 生成token
     */
    public static String getToken(Map<String, String> map) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, 30);
        // 创建 JWT Builder
        JWTCreator.Builder builder = JWT.create();
        // payload
        map.forEach(builder::withClaim);
        // sign
        return builder.withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(SIGN));
    }

    /**
     * 验证token的合法性
     */
    public static DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
    }
}
