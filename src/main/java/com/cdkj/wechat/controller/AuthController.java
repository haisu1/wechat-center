/**
 * project name:platform
 * file name:AuthController
 * package name:com.cdkj.wechat.controller
 * date:2017/12/7 下午3:55
 * author:bovine
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.cdkj.wechat.controller;

import com.cdkj.wechat.service.WeChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * description: 授权控制器 <br>
 * date: 2017/12/7 下午3:55
 *
 * @author zhaozheng
 * @version 1.0
 * @since JDK 1.8
 */

@RequestMapping("wechat/auth")
@Controller
public class AuthController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private WeChatService weChatService;

    //    @RequiresPermissions("wechat:auth")
    @GetMapping()
    String auth(Model model) {
        return "wechat/auth";
    }

    @GetMapping(value = "url")
    String getAuthUrl() {
        return weChatService.getUrlWeChatAuth();
    }
}
