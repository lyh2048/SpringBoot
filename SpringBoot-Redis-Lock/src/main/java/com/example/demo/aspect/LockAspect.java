package com.example.demo.aspect;

import com.example.demo.annotation.CacheLock;
import com.example.demo.interceptor.CacheKeyGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

@Aspect
@Configuration
public class LockAspect {
    @Autowired
    private StringRedisTemplate lockRedisTemplate;

    @Autowired
    private CacheKeyGenerator cacheKeyGenerator;

    @Around("execution(public * *(..)) && @annotation(com.example.demo.annotation.CacheLock)")
    public Object interceptor(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        CacheLock lock = method.getAnnotation(CacheLock.class);
        if (!StringUtils.hasText(lock.prefix())) {
            throw new RuntimeException("lock key is null...");
        }
        final String lockKey = cacheKeyGenerator.getLockKey(pjp);
        try {
            // 采用原生API来实现分布式锁
            final Boolean success = lockRedisTemplate.execute((RedisCallback<Boolean>) connection -> connection.set(lockKey.getBytes(), new byte[0], Expiration.from(lock.expire(), lock.timeUnit()), RedisStringCommands.SetOption.SET_IF_ABSENT));
            if (success == false) {
                // TODO: 这里可以自定义异常
                throw new RuntimeException("请勿重复请求");
            }
            try {
                return pjp.proceed();
            } catch (Throwable throwable) {
                throw new RuntimeException("系统异常");
            }
        } finally {
            // TODO: 如果演示的话需要注释该代码;实际应该放开
             lockRedisTemplate.delete(lockKey);
        }
    }
}
