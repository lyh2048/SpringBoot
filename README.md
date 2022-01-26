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

### Spring Boot 整合 Elasticsearch

Elasticsearch 是一个`实时`的`分布式存储`、`搜索`、`分析`的引擎。

Elasticsearch的一些常见术语：

- Index：Elasticsearch的Index相当于数据库的Table
- Type：这个在新的Elasticsearch版本已经废除（在以前的Elasticsearch版本，一个Index下支持多个Type）
- Document：Document相当于数据库的一行记录
- Field：相当于数据库的Column的概念
- Mapping：相当于数据库的Schema的概念
- DSL：相当于数据库的SQL

引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

配置

```java
@Configuration
public class ElasticSearchConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("127.0.0.1", 9200, "http")
                )
        );
    }
}
```

分页——分词——高亮

```java
@Service
public class MovieService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    public boolean parseMovie(String keyword) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(Constants.INDEX_NAME);
        boolean flag = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if (!flag) {
            // 创建索引
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(Constants.INDEX_NAME);
            restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        }
        List<Movie> movieList = HtmlUtils.getMovieList(keyword);
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueMillis(1));
        for (Movie movie : movieList) {
            bulkRequest.add(
                    new IndexRequest(Constants.INDEX_NAME)
                            .source(JSON.toJSONString(movie), XContentType.JSON)
            );
        }
        // 提交
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        boolean hasFailures = false;
        try {
            hasFailures = !bulkResponse.hasFailures();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasFailures;
    }

    public List<Map<String, Object>> searchResults(String keyword, int page, int size) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        if (page <= 1) {
            page = 1;
        }
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest(Constants.INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(page);
        searchSourceBuilder.size(size);
        // 分词
        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(keyword);
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.timeout(TimeValue.timeValueMillis(1));
        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        // 搜索
        searchRequest.source(searchSourceBuilder);
        // 解析结果
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits()) {
            Map<String, HighlightField> highlightFieldMap = hit.getHighlightFields();
            HighlightField title = highlightFieldMap.get("title");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (title != null) {
                Text[] fragments = title.fragments();
                StringBuilder builder = new StringBuilder();
                for (Text text : fragments) {
                    builder.append(text);
                }
                // 替换
                sourceAsMap.put("title", builder.toString());
            }
            list.add(sourceAsMap);
        }
        return list;
    }
}
```



[⬆回到顶部](#内容)
