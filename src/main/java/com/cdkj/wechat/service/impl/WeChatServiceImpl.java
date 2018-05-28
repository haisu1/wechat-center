/**
 * project name:platform
 * file name:WeChatServiceImpl
 * package name:com.cdkj.wechat.service.Impl
 * date:2017/12/8 上午11:52
 * author:bovine
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.cdkj.wechat.service.Impl;

import com.cdkj.asset.model.pojo.AstAssetType;
import com.cdkj.commom.util.JsonUtils;
import com.cdkj.redis.RedisKeys;
import com.cdkj.redis.service.RedisService;
import com.cdkj.wechat.service.WeChatService;
import com.cdkj.wechat.service.WechatFuncService;
import com.cdkj.wechat.util.MsgProcessUtil;
import com.cdkj.wechat.util.WeChatUtil;
import com.cdkj.wechat.util.aes.WXBizMsgCrypt;
import com.cdkj.wechat.util.aes.WeChatConstants;
import com.cdkj.wechat.util.aes.XMLParse;
import com.cdkj.wechat.util.msg.InMsgParser;
import com.cdkj.wechat.util.msg.in.InMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * description: 微信相关服务信息 <br>
 * date: 2017/12/8 上午11:52
 *
 * @author zhaozheng
 * @version 1.0
 * @since JDK 1.8
 */
@Service
@PropertySource("classpath:wechat.properties")
public class WeChatServiceImpl implements WeChatService {
    protected Logger logger = LoggerFactory.getLogger(WeChatServiceImpl.class);

    @Value("${wechat.token}")
    private String componentToken;
    @Value("${wechat.encoding.aes.key}")
    private String encodingAesKey;
    @Value("${wechat.appid}")
    private String componentAppId;
    @Value("${wechat.appsecret}")
    private String appSecret;
    @Value("${wechat.auth.redirect_uri}")
    private String redirectUri;

    @Resource
    private RedisService redisService;
    @Resource
    private WechatFuncService wechatFuncService;

    @Override
    public String receiveWeChatMessage(String timestamp, String nonce, String msgSignature, String postData) {

        logger.debug("============================================");
        logger.debug(timestamp);
        logger.debug(nonce);
        logger.debug(msgSignature);
        logger.debug(postData);
        logger.debug("============================================");
        WXBizMsgCrypt pc;
        try {
            pc = new WXBizMsgCrypt(componentToken, encodingAesKey, componentAppId);
            String result2 = pc.decryptMsg(msgSignature, timestamp, nonce, postData);
            logger.debug("\n解密后明文: \n" + result2);
            //获得Ticket 信息
            Map<String, String> map = XMLParse.xmlToMap(result2);
//            根据INFO_TYPE 判断是什么类型事件
            if (map.containsKey(WeChatConstants.WECHAT_INFO_TYPE)) {
                String infoType = map.get("InfoType");
                switch (infoType) {
                    case "component_verify_ticket":
                        getComponentVerifyTicket(map);
                        break;
                    case "authorized":
                        getAuthorizedInfo(map);
                        break;
                    case "undateauthorized":
                        getUpdateAuthorizedInfo(map);
                        break;
                    case "unauthorized":
                        getUnauthorizedInfo(map);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            logger.error("receiveWeChatMessage error :{}", e);
        }
        return "success";
    }

    @Override
    public String receiveWeChatAppMessage(String timestamp, String nonce, String msgSignature, String appId, String postData) {
        logger.debug("============================================");
        logger.debug(timestamp);
        logger.debug(nonce);
        logger.debug(msgSignature);
        logger.debug(appId);
        logger.debug(postData);
        logger.debug("============================================");
        WXBizMsgCrypt pc;
        try {
            pc = new WXBizMsgCrypt(componentToken, encodingAesKey, componentAppId);
            String result2 = pc.decryptMsg(msgSignature, timestamp, nonce, postData);
            logger.debug(" \n消息接收解密后明文:\n" + result2);
            InMsg inMsg = InMsgParser.parse(result2);
            MsgProcessUtil msgProcessUtil = new MsgProcessUtil();
            String message = msgProcessUtil.receiveMsg(inMsg);
            //回复消息，注意需要加密发送！
            return pc.encryptMsg(message, timestamp, nonce);
        } catch (Exception e) {
            logger.error("receiveWeChatAppMessage error :{}", e);
        }
        return "success";
    }


    /**
     * 微信公众平台推送过来的票据信息
     *
     * @param map 数据信息
     */
    private void getComponentVerifyTicket(Map<String, String> map) {
//        <xml>
//            <AppId><![CDATA[wx6001eeeefe06e0af]]></AppId>
//            <CreateTime>1512627534</CreateTime>
//            <InfoType><![CDATA[component_verify_ticket]]></InfoType>
//            <ComponentVerifyTicket><![CDATA[ticket@@@5HCM8qgXR3V4h6Q5UChqzxi0A_4s3YWLDC1xcrQA_tMAS6vZUnJXOUYPRjBBFM0S3vGkHsOz8U9atOZTq0roeg]]></ComponentVerifyTicket>
//        </xml>
        //需要根据结果处理相关逻辑，判断节点数据
        String ticket = map.get("ComponentVerifyTicket");
        String appId = map.get("AppId");

        logger.debug("ticket :" + ticket);
        if (StringUtils.isEmpty(ticket)) {
            logger.debug("ComponentVerifyTicket get failed");
        } else {
            redisService.set(RedisKeys.WECHAT_TICKET + appId, ticket, 600);
            logger.debug("redis ticket:" + redisService.get(RedisKeys.WECHAT_TICKET + appId));
        }
    }


    /**
     * 获取授权成功信息
     *
     * @param map 推送数据
     * @return 授权信息
     */
    private String getAuthorizedInfo(Map<String, String> map) {
//        同意授权事件
        //<xml>
        //    <AppId><![CDATA[wx6001eeeefe06e0af]]></AppId>
        //    <CreateTime>1512552185</CreateTime>
        //    <InfoType><![CDATA[authorized]]></InfoType>
        //    <AuthorizerAppid><![CDATA[wxf7d2ddffc117e8de]]></AuthorizerAppid>
        //    <AuthorizationCode><![CDATA[queryauthcode@@@y-s2Y--e_oDQxH_PNnGOGGq_4Kho1fVlJU58YHqp4TqAcZfQjsAbASpu77TpfUrZKumCl5hMFjlBrh31C30e6Q]]></AuthorizationCode>
        //    <AuthorizationCodeExpiredTime><![CDATA[1512555785]]></AuthorizationCodeExpiredTime>
        //    <PreAuthCode><![CDATA[preauthcode@@@cy2CSaTTstMDNRMQK_le2DX9qEUiwEIsqTCpbyrVaf34M49hoE47kCoMM0FogkCN]]></PreAuthCode>
        //</xml>
//        <xml>
//            <AppId>第三方平台appid</AppId>
//            <CreateTime>1413192760</CreateTime>
//            <InfoType>authorized</InfoType>
//            <AuthorizerAppid>公众号appid</AuthorizerAppid>
//            <AuthorizationCode>授权码（code）</AuthorizationCode>
//            <AuthorizationCodeExpiredTime>过期时间</AuthorizationCodeExpiredTime>
//            <PreAuthCode>预授权码</PreAuthCode>
//        </xml>

        String authorizerAppid = map.get("AuthorizerAppid");
        String authorizationCode = map.get("AuthorizationCode");
        String authorizationCodeExpiredTime = map.get("AuthorizationCodeExpiredTime");
        String preAuthCode = map.get("PreAuthCode");

        logger.debug("authorized authorizerAppid :{}", authorizerAppid);
        logger.debug("authorized authorizationCode :{}", authorizationCode);
        logger.debug("authorized authorizationCodeExpiredTime :{}", authorizationCodeExpiredTime);
        logger.debug("authorized preAuthCode :{}", preAuthCode);
//        拿到票据换取公众号授权信息
        String result = WeChatUtil.getUrlWechatApiQueryAuth(componentAppId, getWeChatApiComponentToken(), authorizationCode);

        logger.debug("api query auth :{}", result);
        //TODO 根据拿到的授权数据，获取相关的权限等信息

        //所有的信息必须存档，部分数据是永久持有的。在用户授权时获得，后续需要重复授权才能处理；

        String authorizerInfo = getAuthorizerInfo(authorizerAppid);
        logger.debug("api get authorizerInfo:{}",authorizerInfo);
        // 记录授权用户的详细信息包括二维码
        recordAuthInfo(result,authorizerInfo);
        //TODO 通知管理人员，有公众号关注绑定了

        return "";
    }

    private void recordAuthInfo(String result, String authorizerInfo) {
        Map<String, Object> resultMap = JsonUtils.getMaptoFromJson(result);
        //授权用户详细信息
        Map<String,Object> authInfoMap =JsonUtils.getMaptoFromJson(authorizerInfo);
        String authorizerAccessToken = resultMap.get("authorizer_access_token").toString();
        String authorizerRefreshToken = resultMap.get("authorizer_refresh_token").toString();
        String authorizerAppid = resultMap.get("authorizer_appid").toString();
        //存储用户access_token
        redisService.set(RedisKeys.WECHAT_AUTH_ACCESS_TOKEN+authorizerAppid,authorizerAccessToken,7100);

        //权限列表
        List<Map<String,Object>> funcInfo = (List<Map<String,Object>>)resultMap.get("func_info");

        logger.debug("func_info:",funcInfo);
        //新增或更新授权信息
        wechatFuncService.addOrUpdateSrvFuncInfo(authorizerAccessToken,authorizerRefreshToken,authorizerAppid,funcInfo,authInfoMap);

    }
    /**
     * 获取授权成功信息
     *
     * @param map 推送数据
     * @return 授权信息
     */
    private String getUpdateAuthorizedInfo(Map<String, String> map) {
//        <xml>
//            <AppId>第三方平台appid</AppId>
//            <CreateTime>1413192760</CreateTime>
//            <InfoType>updateauthorized</InfoType>
//            <AuthorizerAppid>公众号appid</AuthorizerAppid>
//            <AuthorizationCode>授权码（code）</AuthorizationCode>
//            <AuthorizationCodeExpiredTime>过期时间</AuthorizationCodeExpiredTime>
//            <PreAuthCode>预授权码</PreAuthCode>
//        </xml>
        return getAuthorizedInfo(map);
    }

    /**
     * 获取授权取消信息
     *
     * @param map 授权取消信息
     * @return
     */
    private String getUnauthorizedInfo(Map<String, String> map) {

//          取消授权事件
//        <xml>
//            <AppId><![CDATA[wx6001eeeefe06e0af]]></AppId>
//            <CreateTime>1512552130</CreateTime>
//            <InfoType><![CDATA[unauthorized]]></InfoType><AuthorizerAppid><![CDATA[wxf7d2ddffc117e8de]]></AuthorizerAppid>
//        </xml>
        String authorizerAppid = map.get("AuthorizerAppid");

        logger.debug("unauthorized authorizerAppid :{}", authorizerAppid);
        return authorizerAppid;
    }


    public String getWeChatApiComponentToken() {
        logger.debug("getWeChatApiComponentToken..........................start");
        String wechatApiComponentToken = redisService.get(RedisKeys.WECHAT_API_COMPONENT_TOKEN + componentAppId);

        if (StringUtils.isEmpty(wechatApiComponentToken)) {
            //获取新的TOKEN
            wechatApiComponentToken = WeChatUtil.getWeChatApiComponentToken(componentAppId, appSecret, redisService.get(RedisKeys.WECHAT_TICKET + componentAppId));
            //小于官方的7200，便于防止失效
            if (!StringUtils.isEmpty(wechatApiComponentToken)) {
                redisService.set(RedisKeys.WECHAT_API_COMPONENT_TOKEN + componentAppId, wechatApiComponentToken, 7100);
            }
            return wechatApiComponentToken;
        }
        logger.debug("getWeChatApiComponentToken..........................end");
        return wechatApiComponentToken;
    }

    public String getPreAuthCode() {
        logger.debug("getPreAuthCode..........................start");
        String preAuthCode = redisService.get(RedisKeys.WECHAT_PRE_AUTH_CODE + componentAppId);
        if (StringUtils.isEmpty(preAuthCode)) {
            String token = redisService.get(RedisKeys.WECHAT_API_COMPONENT_TOKEN + componentAppId);
            if (StringUtils.isEmpty(token)) {
                token = getWeChatApiComponentToken();
            }
            preAuthCode = WeChatUtil.getPreAuthCode(token, componentAppId);
            //小于官方的1200，便于防止时间差失效
            if (!StringUtils.isEmpty(preAuthCode)) {
                redisService.set(RedisKeys.WECHAT_PRE_AUTH_CODE + componentAppId, preAuthCode, 1150);
            }
        }
        logger.debug("getPreAuthCode..........................start");
        return preAuthCode;
    }

    /**
     * 获取微信授权URL信息
     *
     * @return
     */
    @Override
    public String getUrlWeChatAuth() {
        logger.debug("getUrlWeChatAuth..........................");
        return WeChatUtil.getUrlWeChatAuth(componentAppId, getPreAuthCode(), redirectUri, "1");
    }

    /**
     * 获取授权公众号详细信息
     *
     * @param authorizerAppId 授权公众号AppId
     * @return 公众号信息
     */
    @Override
    public String getAuthorizerInfo(String authorizerAppId) {
        logger.debug("getAuthorizerInfo..........................");
        return WeChatUtil.getAuthorizerInfo(getWeChatApiComponentToken(), componentAppId, authorizerAppId);
    }
}
