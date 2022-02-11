package com.example.demo.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheLock {
    /**
     * key的前缀
     */
    String prefix() default "";
    /**
     * 过期秒数，默认5s
     */
    int expire() default 5;
    /**
     * 超时时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    /**
     * key的分隔符号
     */
    String delimiter() default ":";
}
