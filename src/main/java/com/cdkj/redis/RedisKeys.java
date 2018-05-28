/**
 * project name:platform
 * file name:RedisKeys
 * package name:com.cdkj.redis
 * date:2017/12/8 上午11:31
 * author:bovine
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.cdkj.redis;

/**
 * description: redis key信息 <br>
 * date: 2017/12/8 上午11:31
 *
 * @author zhaozheng
 * @version 1.0
 * @since JDK 1.8
 */
public interface RedisKeys {

    String WECHAT_TICKET = "WECHAT_TICKET-";
    String WECHAT_API_COMPONENT_TOKEN = "WECHAT_API_COMPONENT_TOKEN-";
    String WECHAT_PRE_AUTH_CODE = "WECHAT_PRE_AUTH_CODE-";
    String WECHAT_AUTH_ACCESS_TOKEN="WECHAT_AUTH_ACCESS_TOKEN-";

}
