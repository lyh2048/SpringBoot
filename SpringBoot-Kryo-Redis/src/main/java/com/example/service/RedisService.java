package com.example.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisService {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private RedisConnection getConnection() {
        return redisTemplate.getConnectionFactory().getConnection();
    }

    /**
     * 释放连接
     */
    private void releaseConnection(RedisConnection redisConnection) {
        if (null != redisConnection && null != redisTemplate) {
            RedisConnectionFactory redisConnectionFactory = redisTemplate.getConnectionFactory();
            if (null != redisConnectionFactory) {
                RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
            }
        }
    }

    /**
     * 获取缓存的key
     */
    private <T> byte[] getKey(T id) {
        RedisSerializer serializer = redisTemplate.getKeySerializer();
        return serializer.serialize(id);
    }

    /**
     * 更新缓存中的对象，也可以在redis缓存中存入新的对象
     */
    public <T> void set(String key , T t) {
        byte[] keyBytes = getKey(key);
        RedisSerializer serializer = redisTemplate.getValueSerializer();
        byte[] val = serializer.serialize(t);
        RedisConnection redisConnection = getConnection();
        if (null != redisConnection) {
            try {
                redisConnection.set(keyBytes, val);
            } finally {
                releaseConnection(redisConnection);
            }
        } else {
            log.error("can not get valid connection");
        }
    }

    /**
     * 删除指定对象
     */
    public long del(String key) {
        RedisConnection redisConnection = getConnection();
        long result = 0L;
        if (null != redisConnection) {
            try {
                result = redisConnection.del(getKey(key));
            } finally {
                releaseConnection(redisConnection);
            }
        } else {
            log.error("can not get valid connection");
        }
        return result;
    }

    /**
     *  从缓存中取对象
     */
    public <T> T getObjects(String key) {
        byte[] keyBytes = getKey(key);
        byte[] result = null;
        RedisConnection redisConnection = getConnection();
        if (null != redisConnection) {
            try {
                result = redisConnection.get(keyBytes);
            } finally {
                releaseConnection(redisConnection);
            }
        } else {
            log.error("can not get valid connection");
        }
        return null != redisConnection ? (T) redisTemplate.getValueSerializer().deserialize(result) : null;
    }
}
