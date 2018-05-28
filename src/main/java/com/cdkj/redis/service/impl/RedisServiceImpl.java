/**
 * project name:platform
 * file name:RedisServiceImpl
 * package name:com.cdkj.redis.service.impl
 * date:2017/12/8 上午11:18
 * author:bovine
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.cdkj.redis.service.impl;

import com.alibaba.fastjson.JSON;
import com.cdkj.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * description: redis 封装类 <br>
 * date: 2017/12/8 上午11:18
 *
 * @author zhaozheng
 * @version 1.0
 * @since JDK 1.8
 */
@Service
public class RedisServiceImpl implements RedisService {

    private static final long EXPIRE_TIME = 60 * 60 * 2;
    @Autowired
    private RedisTemplate<String, ?> redisTemplate;

    @Override
    public boolean set(final String key, final String value) {
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            connection.set(serializer.serialize(key), serializer.serialize(value));
            return true;
        });
    }

    @Override
    public boolean set(String key, String value, long expire) {
        boolean tag = set(key, value);
        return tag && expire(key, expire);
    }

    @Override
    public String get(final String key) {
        return redisTemplate.execute((RedisCallback<String>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(serializer.serialize(key));
            return serializer.deserialize(value);
        });
    }

    @Override
    public boolean expire(final String key, long expire) {
        return redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }
//
//    @Override
//    public <T> boolean setList(String key, List<T> list) {
//        String value = JSON.toJSONString(list);
//        return set(key,value);
//    }

//    @Override
//    public <T> List<T> getList(String key,Class<T> clz) {
//        String json = get(key);
//        if(json!=null){
//            List<T> list = JSONUtils.toList(json, clz);
//            return list;
//        }
//        return null;
//    }

    @Override
    public long lpush(final String key, Object obj) {
        final String value = JSON.toJSONString(obj);
        long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            long count = connection.lPush(serializer.serialize(key), serializer.serialize(value));
            return count;
        });
        return result;
    }

    @Override
    public long rpush(final String key, Object obj) {
        final String value = JSON.toJSONString(obj);
        long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            long count = connection.rPush(serializer.serialize(key), serializer.serialize(value));
            return count;
        });
        return result;
    }

    @Override
    public String lpop(final String key) {
        String result = redisTemplate.execute((RedisCallback<String>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] res = connection.lPop(serializer.serialize(key));
            return serializer.deserialize(res);
        });
        return result;
    }
}
