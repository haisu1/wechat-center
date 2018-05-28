/**
 * project name:platform
 * file name:RedisService
 * package name:com.cdkj.redis.service
 * date:2017/12/8 上午11:17
 * author:bovine
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.cdkj.redis.service;

import org.springframework.stereotype.Service;

/**
 * description: redis 封装类 <br>
 * date: 2017/12/8 上午11:17
 *
 * @author zhaozheng
 * @version 1.0
 * @since JDK 1.8
 */
@Service
public interface RedisService {
    public boolean set(String key, String value);

    public boolean set(String key, String value, long expire);

    public String get(String key);

    public boolean expire(String key, long expire);

//    public <T> boolean setList(String key ,List<T> list);

//    public <T> List<T> getList(String key,Class<T> clz);

    public long lpush(String key, Object obj);

    public long rpush(String key, Object obj);

    public String lpop(String key);
}
