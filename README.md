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

