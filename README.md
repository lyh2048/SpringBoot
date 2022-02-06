# Spring Boot



[![Spring](assets/spring-logo-9146a4d3298760c2e7e49595184e1975.svg)](https://spring.io/projects/spring-boot)



* [Spring Boot](#spring-boot)
  * [简介](#简介)
  * [内容](#内容)
    * [Spring Boot 整合 Redis](#spring-boot-整合-redis)
    * [Spring Boot 使用Kryo作为Redis序列化工具](#spring-boot-使用kryo作为redis序列化工具)
    * [Spring Boot 整合 Elasticsearch](#spring-boot-整合-elasticsearch)
    * [Spring Boot 整合 JWT](#spring-boot-整合-jwt)
    * [基于Spring Security 和 JWT 的权限系统](#基于spring-security-和-jwt-的权限系统)
    * [Spring Boot 使用Guava Cache本地缓存](#spring-boot使用guava-cache本地缓存)
    * [Spring Boot 集成Shiro权限管理](#spring-boot-集成shiro权限管理)
    * [Spring Boot JPA 入门](#spring-boot-jpa-入门)
    * [Spring Boot 使用JdbcTemplate访问数据库](#Spring-Boot-使用JdbcTemplate访问数据库)
    * [Spring Boot 整合Thymeleaf模板](#Spring-Boot-整合Thymeleaf模板)
    * [Spring Boot Admin](#Spring-Boot-Admin)
    * [Spring Boot 文件上传](#Spring-Boot-文件上传)
    * [Spring Boot 整合 WebSocket](#Spring-Boot-整合-WebSocket)
    * [Spring Boot 使用 FreeMarker 模板引擎](#Spring-Boot-使用-FreeMarker-模板引擎)

## 简介

Spring Boot 与时下流行技术的整合

> 由于能力有限，若有错误或者不当之处，还请大家批评指正，一起学习交流！

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

### Spring Boot 整合 JWT

Json Web Token（JWT）是一个开放标准（RFC 7519），它定义了一种紧凑且自包含的方式，用于在各方之间安全地将信息作为JSON对象传输。由于此信息是经过数字签名的，因此可以被验证和信任。

作用：

- 授权：这是使用JWT的最常见方案。一旦用户登录，每个后续请求将包括JWT，从而允许用户访问该令牌允许的路由，服务和资源。单点登录是当今广泛使用JWT的一项功能，因为它的开销很小并且可以在不同的域中轻松使用。
- 信息交换：Json Web令牌是在各方之间安全地传输信息的一种好方法。因为可以对JWT进行签名（例如，使用公钥/私钥对），所以您可以确保发件人是他们所说的人。此外，由于签名是使用标头和有效负载计算的，因此，您还可以验证内容是否遭到篡改。

Json Web令牌以紧凑的形式由三部分组成，这些部分由点（`.`）分隔，分别是：

- 标头
- 有效载荷
- 签名

解决每个方法都要验证token的代码冗余问题：

在分布式里可以使用网关，在单体应用下可以使用拦截器`Interceptor`



[⬆回到顶部](#内容)

### 基于Spring Security 和 JWT 的权限系统

Spring Security 可以为Spring应用提供声明式的安全访问控制，提供了一系列可以在Spring应用上下文中可配置的Bean，并利用Spring IOC 和 AOP等功能特性来为应用系统提供声明式的安全访问控制功能，减少了诸多重复工作。

角色表`role`

| id   | name        |
| ---- | ----------- |
| 1    | ROLE_NORMAL |
| 2    | ROLE_ADMIN  |

用户表`user`

| id   | username | password                                                     |
| ---- | -------- | ------------------------------------------------------------ |
| 1    | lyh      | `$2a$10$3wz3ngnLKwJKIv4SIRtqReynm/bNzLFWov.nK0XvXnjXcPGjgnDf.` |

用户和角色一对多的关联表`user_roles`

| user_id | roles_id |
| ------- | -------- |
| 1       | 1        |
| 1       | 2        |

Token过滤器

```java
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(Constants.HEADER_STRING);
        if (null != authHeader && authHeader.startsWith(Constants.TOKEN_PREFIX)) {
            String authToken = authHeader.substring(Constants.TOKEN_PREFIX.length());
            String username = jwtTokenUtils.getUsernameFromToken(authToken);
            if (null != username && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtils.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

参考资料：

[https://mp.weixin.qq.com/s/sMi1__Rw_s75YDaIdmTWKw](https://mp.weixin.qq.com/s/sMi1__Rw_s75YDaIdmTWKw)



[⬆回到顶部](#内容)

### Spring Boot使用Guava Cache本地缓存

> Guava Cache 是一个全内存的本地缓存实现，而且提供了线程安全机制，所以特别适合于代码中已经预料到某些值会被多次调用的场景

`@Cacheable`：配置在方法上表示其返回值将被加入缓存，同时在查询时，会先从缓存中获取，若不存在才发起对数据库的访问

`@CachePut`：配置于方法上时，能够根据参数定义条件来进行缓存，其与`@Cacheable`不同的是使用`@CachePut`标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中，所以主要用于数据新增和修改操作上

`@CacheEvict`： 配置于方法上，表示从缓存中移除相应的数据

```java
@Configuration
@EnableCaching
public class GuavaCacheConfig {
    @Bean
    public CacheManager cacheManager() {
        GuavaCacheManager cacheManager = new GuavaCacheManager();
        cacheManager.setCacheBuilder(
                CacheBuilder.newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .maximumSize(1000)
        );
        return cacheManager;
    }
}
```



[⬆回到顶部](#内容)

### Spring Boot 集成Shiro权限管理

`Apache Shiro`是一个Java的安全框架，可以帮助我们完成：认证、授权、加密、会话管理、与Web集成、缓存等。

实现用户登录认证和授权访问页面

测试账号：

| 用户名 | 密码     |
| ------ | -------- |
| admin  | 12345678 |
| aix    | 12345678 |

根据登录用户的不同，显示不同的菜单项

admin登录后可以看到用户管理菜单，aix登录后可以看到应用管理等业务菜单

参考资料

[https://www.xncoding.com/2017/07/07/spring/sb-shiro.html](https://www.xncoding.com/2017/07/07/spring/sb-shiro.html)



[⬆回到顶部](#内容)

### Spring Boot JPA 入门

[Spring Data JPA](https://spring.io/projects/spring-data-jpa)

JPA，全称Java Persistence API，是由Java定义的Java ORM以及实体操作API标准。正如最早学习JDBC规范，Java自身并未提供相关的实现，而是MySQL提供MySQL `mysql-connector-java`驱动，Oracle提供`oracle-jdbc`驱动，而实现JPA规范的有：

- Hibernate ORM
- Oracle TopLink
- Apache OpenJPA

Spring Data JPA，是Spring Data提供的一套简化的JPA开发框架。

- 内置CRUD、分页、排序等功能的操作
- 根据约定好的方法名规则，自动生成对应的查询操作
- 使用`@Query`注解，自定义SQL

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```



参考资料

[https://www.iocoder.cn/Spring-Boot/JPA/](https://www.iocoder.cn/Spring-Boot/JPA/)



[⬆回到顶部](#内容)

### Spring Boot 使用JdbcTemplate访问数据库

`Spring Framework`对数据库的操作在`JDBC`上面做了深层次的封装，通过依赖注入功能，可以将`DataSource`注册到`JdbcTemplate`之中，使我们可以轻易的完成对象关系映射，并有助于规避常见的错误，在`Spring Boot`中，我们可以很轻松的使用它。

特点：

- 速度快，对比其它的ORM框架，JDBC的方式无异于是最快的
- 配置简单，`Spring`自家出品，几乎没有额外配置
- 学习成本低，毕竟`JDBC`是基础知识，`JdbcTemplate`更像是一个`DBUtils`

参考资料：

[https://www.iocoder.cn/Spring-Boot/battcn/v2-orm-jdbc/](https://www.iocoder.cn/Spring-Boot/battcn/v2-orm-jdbc/)



[⬆回到顶部](#内容)

### Spring Boot 整合Thymeleaf模板

`Thymeleaf`是现代化服务器端的Java模板引擎，不同与其它几种模板的是`Thymeleaf`的语法更加接近HTML，并且具有很高的扩展性。详细资料可以浏览[官网](https://www.thymeleaf.org/)。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```



[⬆回到顶部](#内容)

### Spring Boot Admin

> SBA 全称 Spring Boot Admin 是一个管理和监控 Spring Boot 应用程序的开源项目。分为admin-server 与 admin-client 两个组件，admin-server通过采集 actuator 端点数据，显示在 spring-boot-admin-ui 上，已知的端点几乎都有进行采集，通过 spring-boot-admin 可以动态切换日志级别、导出日志、监控各项指标……
>
> `Spring Boot Admin` 在对单一应用服务监控的同时也提供了集群监控方案，支持通过`eureka`、`consul`、`zookeeper`等注册中心的方式实现多服务监控与管理……

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
</dependency>
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
</dependency>
<dependency>
    <groupId>org.jolokia</groupId>
    <artifactId>jolokia-core</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```



[⬆回到顶部](#内容)

### Spring Boot 文件上传

文件上传和下载是`Java Web`中常见的操作，文件上传主要是`将文件通过IO流传输到服务器的某一个特定的文件夹下`

```java
/**
 * 文件上传
 */

@RestController
@RequestMapping("/uploads")
public class FileUploadController {
    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);
    private static final String SAVE_PATH = "D:/Temp/";

    @PostMapping("/upload1")
    public Map<String, Object> upload1(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("[文件类型] - [{}]", file.getContentType());
        log.info("[文件名称] - [{}]", file.getOriginalFilename());
        log.info("[文件大小] - [{}]", file.getSize());
        file.transferTo(new File(SAVE_PATH + file.getOriginalFilename()));
        Map<String, Object> result = new HashMap<>();
        result.put("contentType", file.getContentType());
        result.put("fileName", file.getOriginalFilename());
        result.put("fileSize", file.getSize() + "");
        return result;
    }

    @PostMapping("/upload2")
    public List<Map<String, Object>> upload2(@RequestParam("file") MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            return null;
        }
        List<Map<String, Object>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            Map<String, Object> map = new HashMap<>();
            map.put("contentType", file.getContentType());
            map.put("fileName", file.getOriginalFilename());
            map.put("fileSize", file.getSize() + "");
            file.transferTo(new File(SAVE_PATH + file.getOriginalFilename()));
            results.add(map);
        }
        return results;
    }

    @PostMapping("/upload3")
    public String upload3(String base64) throws IOException {
        // TODO BASE64 方式的 格式和名字需要自己控制（如 png 图片编码后前缀就会是 data:image/png;base64,）
        final File tempFile = new File(SAVE_PATH + "test.jpg");
        // TODO 防止有的传了 data:image/png;base64, 有的没传的情况
        String[] d = base64.split("base64,");
        final byte[] bytes = Base64Utils.decodeFromString(d.length > 1 ? d[1] : d[0]);
        FileCopyUtils.copy(bytes, tempFile);
        return "上传成功";
    }
}
```



[⬆回到顶部](#内容)

### Spring Boot 整合 WebSocket

WebSocket是HTML5新增的一种在单个TCP连接上进行全双工通讯的协议，与HTTP协议没有太大的关系……

在WebSocket API中，浏览器和服务器只需要做一个握手的动作，然后，浏览器和服务器之间就形成了一条快速通道。两者之间就可以直接数据相互传送。

```java
@RestController
@ServerEndpoint("/chat-room/{username}")
@Slf4j
public class ChatRoomServer {
    @GetMapping("/chat-room/{sender}/to/{receiver}")
    public void onMessage(@PathVariable("sender") String sender, @PathVariable("receiver") String receiver, String message) {
        WebSocketUtils.sendMessage(WebSocketUtils.LIVING_SESSIONS_CACHE.get(receiver), "[" + sender + "]" + "-> [" + receiver + "] : " + message);
    }

    @OnOpen
    public void openSession(@PathParam("username") String username, Session session) {
        WebSocketUtils.LIVING_SESSIONS_CACHE.put(username, session);
        String message = "欢迎用户[" + username + "] 来到聊天室！";
        log.info(message);
        WebSocketUtils.sendMessageAll(message);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        log.info(message);
        WebSocketUtils.sendMessageAll("用户[" + username + "] : " + message);
    }

    @OnClose
    public void onClose(@PathParam("username") String username, Session session) {
        WebSocketUtils.LIVING_SESSIONS_CACHE.remove(username);
        WebSocketUtils.sendMessageAll("用户[" + username + "] 已经离开聊天室了！");
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void OnError(Session session, Throwable throwable) {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throwable.printStackTrace();
    }
}
```



[⬆回到顶部](#内容)

### Spring Boot 使用 FreeMarker 模板引擎

FreeMarker 是一款用Java语言编写的模板引擎，它是基于模板文件生成其他文本的通用工具。

特点：

- 轻量级模板，不需要Servlet环境就可以很轻松的嵌入到应用程序中
- 能生成各种文本，如html、xml、java等
- 入门简单，它是用`Java`编写的，很多语法和`Java`相似

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-freemarker</artifactId>
</dependency>
```



[⬆回到顶部](#内容)
