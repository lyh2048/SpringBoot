package com.example.demo.aspect;

import com.example.demo.annotation.Limit;
import com.example.demo.enums.LimitType;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Method;

@Aspect
@Configuration
public class LimitAspect {
    private static final Logger log = LoggerFactory.getLogger(LimitAspect.class);
    private static final String UNKNOWN = "unknown";

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    private String getIpAddress() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteHost();
        }
        return ip;
    }

    /**
     * 限流 脚本
     * @return lua脚本
     */
    private String luaScript() {
        return "local c" +
                "\nc = redis.call('get',KEYS[1])" +
                // 调用不超过最大值，则直接返回
                "\nif c and tonumber(c) > tonumber(ARGV[1]) then" +
                "\nreturn c;" +
                "\nend" +
                // 执行计算器自加
                "\nc = redis.call('incr',KEYS[1])" +
                "\nif tonumber(c) == 1 then" +
                // 从第一次调用开始限流，设置对应键值的过期
                "\nredis.call('expire',KEYS[1],ARGV[2])" +
                "\nend" +
                "\nreturn c;";
    }

    @Around("execution(public * *(..)) && @annotation(com.example.demo.annotation.Limit)")
    public Object interceptor(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        Limit annotation = method.getAnnotation(Limit.class);
        LimitType limitType = annotation.limitType();
        String name = annotation.name();
        String key;
        int limitPeriod = annotation.period();
        int limitCount = annotation.count();
        switch (limitType) {
            case IP:
                key = getIpAddress();
                break;
            case CUSTOMER:
                key = annotation.key();
                break;
            default:
                key = StringUtils.upperCase(method.getName());
        }
        ImmutableList<String> keys = ImmutableList.of(StringUtils.join(annotation.prefix(), key));
        try {
            String lua = luaScript();
            RedisScript<Long> redisScript = new DefaultRedisScript<>(lua, Long.class);
            Number count = redisTemplate.execute(redisScript, keys, limitCount, limitPeriod);
            log.info("Access try count is {} for name={} and key={}", count, name, key);
            if (count != null && count.intValue() <= limitCount) {
                return proceedingJoinPoint.proceed();
            } else {
                throw new RuntimeException("You have been dragged into the blacklist");
            }
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw new RuntimeException(e.getLocalizedMessage());
            }
            throw new RuntimeException("server exception");
        }
    }

}
