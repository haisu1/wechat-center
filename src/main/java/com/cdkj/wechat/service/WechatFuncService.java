/**
 * project name:saas
 * file name:WechatFuncService
 * package name:com.cdkj.wechat.service
 * date:2018-02-01 11:12
 * author:zhaozheng
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.cdkj.wechat.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * description: 微信权限服务 <br>
 * date: 2018-02-01 11:12
 *
 * @author zhaozheng
 * @version 1.0
 * @since JDK 1.8
 */
public interface WechatFuncService {
    /**
     * 新增或更新公众号授权信息
     * @param authorizerAccessToken
     * @param authorizerRefreshToken
     * @param authorizerAppid
     * @param funcInfo
     * @param authInfoMap
     */
    void addOrUpdateSrvFuncInfo(String authorizerAccessToken, String authorizerRefreshToken, String authorizerAppid, List<Map<String, Object>> funcInfo, Map<String, Object> authInfoMap);
}
