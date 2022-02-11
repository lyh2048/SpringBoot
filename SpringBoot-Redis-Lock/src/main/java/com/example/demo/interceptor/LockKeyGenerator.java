package com.example.demo.interceptor;

import com.example.demo.annotation.CacheLock;
import com.example.demo.annotation.CacheParam;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class LockKeyGenerator implements CacheKeyGenerator{
    @Override
    public String getLockKey(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CacheLock annotation = method.getAnnotation(CacheLock.class);
        final Object[] args = pjp.getArgs();
        final Parameter[] parameters = method.getParameters();
        StringBuilder sb = new StringBuilder();
        // TODO: 默认解析方法里面带CacheParam注解的属性，如果没有尝试解析实体对象中的
        for (int i = 0; i < parameters.length; i++) {
            final CacheParam cacheParam = parameters[i].getAnnotation(CacheParam.class);
            if (cacheParam == null) {
                continue;
            }
            sb.append(annotation.delimiter()).append(args[i]);
        }
        if (!StringUtils.hasText(sb.toString())) {
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                final Object object = args[i];
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    final CacheParam cacheParam = field.getAnnotation(CacheParam.class);
                    if (cacheParam == null) {
                        continue;
                    }
                    field.setAccessible(true);
                    sb.append(annotation.delimiter()).append(ReflectionUtils.getField(field, object));
                }
            }
        }
        return annotation.prefix() + sb;
    }
}
