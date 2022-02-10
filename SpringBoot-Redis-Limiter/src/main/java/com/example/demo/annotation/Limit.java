package com.example.demo.annotation;

import com.example.demo.enums.LimitType;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Limit {
    /**
     * 资源的名字
     */
    String name() default "";
    /**
     * 资源的key
     */
    String key() default "";
    /**
     * key的prefix
     */
    String prefix() default "";
    /**
     * 给定的时间段
     * 单位（秒）
     */
    int period();
    /**
     * 最多的访问次数
     */
    int count();
    /**
     * 类型
     */
    LimitType limitType() default LimitType.CUSTOMER;
}
