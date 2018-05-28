/**
 * project name:platform
 * file name:WeChatService
 * package name:com.cdkj.wechat.service
 * date:2017/12/8 上午11:51
 * author:bovine
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.cdkj.wechat.service;

import org.springframework.stereotype.Service;

/**
 * description: //TODO <br>
 * date: 2017/12/8 上午11:51
 *
 * @author zhaozheng
 * @version 1.0
 * @since JDK 1.8
 */
@Service
public interface WeChatService {
    /**
     * 接收微信的通知信息
     *
     * @param timestamp    时间戳
     * @param nonce        随机字符串
     * @param msgSignature 签名
     * @param postData     数据
     * @return
     */
    String receiveWeChatMessage(String timestamp, String nonce,
                                String msgSignature, String postData);

    /**
     * 接收微信的通知信息
     *
     * @param timestamp    时间戳
     * @param nonce        随机字符串
     * @param msgSignature 签名
     * @param appId        微信公众信息
     * @param postData     数据
     * @return
     */
    String receiveWeChatAppMessage(String timestamp, String nonce,
                                   String msgSignature, String appId, String postData);

    /**
     * 获取微信授权URL信息
     *
     * @return
     */
    String getUrlWeChatAuth();

    /**
     * 获取授权公众号详细信息
     *
     * @param authorizerAppId 授权公众号AppId
     * @return 公众号信息
     */
    String getAuthorizerInfo(String authorizerAppId);
}
