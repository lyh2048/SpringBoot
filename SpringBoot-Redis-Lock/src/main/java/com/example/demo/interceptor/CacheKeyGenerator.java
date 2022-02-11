package com.example.demo.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;

public interface CacheKeyGenerator {
    /**
     * 获取AOP参数，生成指定缓存key
     * @param pjp PJP
     * @return 缓存Key
     */
    String getLockKey(ProceedingJoinPoint pjp);
}
