/**
 * project name:platform
 * file name:WechatController
 * package name:com.cdkj.wechat.controller
 * date:2017/12/5 下午1:44
 * author:bovine
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.cdkj.wechat.controller;

import com.cdkj.wechat.service.WeChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * description: 微信第三方相关控制器 <br>
 * date: 2017/12/5 下午1:44
 *
 * @author zhaozheng
 * @version 1.0
 * @since JDK 1.8
 */
@RequestMapping("/wechat/open")
@Controller
public class WeChatController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private WeChatService weChatService;

    /**
     * 接收微信的TICKET信息+授权事件接收
     *
     * @param timestamp    时间戳
     * @param nonce        随机字符串
     * @param msgSignature 签名
     * @param postData     数据
     * @return
     */
    @RequestMapping(value = "/auth/info")
    @ResponseBody
    public String receiveWeChatMessage(@RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce,
                                       @RequestParam("msg_signature") String msgSignature, @RequestBody String postData) {

        return weChatService.receiveWeChatMessage(timestamp, nonce, msgSignature, postData);
    }

    /**
     * 公众号消息与事件接收
     *
     * @param timestamp    时间戳
     * @param nonce        随机字符串
     * @param msgSignature 签名
     * @param appId        测试公众号appId
     * @param postData     数据
     * @return
     */
    @RequestMapping(value = "/{appId}/message/callback")
    @ResponseBody
    public String receiveWeChatAppMessage(@RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce,
                                          @RequestParam("msg_signature") String msgSignature, @PathVariable("appId") String appId,
                                          @RequestBody String postData, HttpServletRequest req, HttpServletResponse resp) {
        PrintWriter out = null;
        String message;
        try {
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");
            out = resp.getWriter();
            //处理微信推送消息
            message = weChatService.receiveWeChatAppMessage(timestamp, nonce, msgSignature, appId, postData);
            logger.debug("message:{}", message);
            assert out != null;
            out.print(message);
            out.close();
        } catch (Exception e) {
            logger.error("message.run error：{}", e);
        } finally {
            if (null != out) {
                out.close();
            }
        }
        return "success";
    }
}
