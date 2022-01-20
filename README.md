# Spring Boot

[![Spring](assets/spring-logo-9146a4d3298760c2e7e49595184e1975.svg)](https://spring.io/projects/spring-boot)



---

## 简介

Spring Boot 与时下流行技术的整合

## 内容

### Spring Boot 整合 Redis

1、引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

2、Redis配置

```yaml
spring:
  redis:
    # Redis服务器地址
    host: 192.168.108.128
    # Redis服务器连接端口
    port: 6379
    # 数据库索引
    database: 0
    # 连接超时时间
    timeout: 1800000
    lettuce:
      pool:
        # 最大连接数
        max-active: 20
        # 最大阻塞时间
        max-wait: 1
        # 最大空闲连接
        max-idle: 5
        # 最小空闲连接
        min-idle: 0
```



3、Redis配置类

```java
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        template.setKeySerializer(redisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(600))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();
        return RedisCacheManager.builder(factory)
                .cacheDefaults(configuration)
                .build();
    }
}
```



4、测试

```java
@RestController
@RequestMapping("/redisTest")
public class RedisTestController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping
    public String testRedis() {
        // 设置值
        redisTemplate.opsForValue().set("name", "lyh");
        // 获取值
        return (String) redisTemplate.opsForValue().get("name");
    }
}
```

[⬆回到顶部](#内容)

### Spring Boot 使用Kryo作为Redis序列化工具

在Java应用中，所有对象的创建都是在内存中完成的，当应用需要保存对象到磁盘文件或通过网络发送给其他应用时，需要将对象信息转化成二进制字节流，这个从对象状态转化成二进制字节流的过程，就是序列化。相反，从字节流创建成对象的过程就是反序列化。

将Java对象实例存入Redis，常用方法有两种：

1. 将对象序列化成字符串后存入Redis
2. 将对象序列化成byte数组后存入Redis

Kryo是一个快速且高效的针对Java对象序列化的框架，它的特点：

- 序列化的性能非常高
- 序列化结果体积小
- 提供简单易用的API

基于Kryo的序列化接口实现类

```java
package com.example.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayOutputStream;

/**
 * 自定义序列化工具类
 */

@Slf4j
public class KryoRedisSerializer<T> implements RedisSerializer<T> {
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(Kryo::new);
    private final Class<T> clazz;

    public KryoRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }


    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return EMPTY_BYTE_ARRAY;
        }
        Kryo kryo = kryos.get();
        kryo.setReferences(false);
        kryo.register(clazz);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos)) {
            kryo.writeClassAndObject(output, t);
            output.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return EMPTY_BYTE_ARRAY;
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Kryo kryo = kryos.get();
        kryo.setReferences(false);
        kryo.register(clazz);
        try (Input input = new Input(bytes)) {
            return (T) kryo.readClassAndObject(input);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}

```



[⬆回到顶部](#内容)

