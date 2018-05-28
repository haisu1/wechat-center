/**
 * project name:saas
 * file name:WechatFuncServiceImpl
 * package name:com.cdkj.wechat.service.Impl
 * date:2018-02-01 11:13
 * author:zhaozheng
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.cdkj.wechat.service.Impl;

import com.cdkj.commom.util.DateUtil;
import com.cdkj.commom.util.GUIDUtil;
import com.cdkj.wechat.model.dao.SrvWechatFuncInfoMapper;
import com.cdkj.wechat.model.dao.SrvWechatMainInfoMapper;
import com.cdkj.wechat.model.pojo.SrvWechatFuncInfo;
import com.cdkj.wechat.model.pojo.SrvWechatMainInfo;
import com.cdkj.wechat.service.WechatFuncService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * description: //TODO <br>
 * date: 2018-02-01 11:13
 *
 * @author zhaozheng
 * @version 1.0
 * @since JDK 1.8
 */
@Service("/wechatFuncServiceImpl")
public class WechatFuncServiceImpl implements WechatFuncService {
    @Resource
    private SrvWechatMainInfoMapper srvWechatMainInfoMapper;
    @Resource
    private SrvWechatFuncInfoMapper srvWechatFuncInfoMapper;

    @Override
    public void addOrUpdateSrvFuncInfo(String authorizerAccessToken, String authorizerRefreshToken, String authorizerAppid, List<Map<String, Object>> funcInfo, Map<String, Object> authInfoMap) {
        SrvWechatMainInfo srvWechatMainInfo = srvWechatMainInfoMapper.selectByAuthAppId(authorizerAppid);
        if (srvWechatMainInfo != null) {
            //删除之前的授权列表
            int delNums = srvWechatFuncInfoMapper.deleteByMainId(srvWechatMainInfo.getId());
            srvWechatMainInfo.setAuthorizerRefreshToken(authorizerRefreshToken);
            srvWechatMainInfo.setUpdateDt(DateUtil.getNow());
            srvWechatMainInfoMapper.updateByPrimaryKeySelective(srvWechatMainInfo);
        } else {
            String nickName = authInfoMap.get("nick_name").toString();
            String headUrl = authInfoMap.get("head_img").toString();
            Map<String, Integer> serviceTypeInfo = (Map<String, Integer>) authInfoMap.get("service_type_info");
            //授权方公众号类型
            Integer serviceTypeInfoId = serviceTypeInfo.get("id");
            Map<String, Integer> verifyTypeInfo = (Map<String, Integer>) authInfoMap.get("verify_type_info");
            //授权方认证类型
            Integer verifyTypeInfoId = verifyTypeInfo.get("id");
            String userName = authInfoMap.get("user_name").toString();
            String principalName = authInfoMap.get("principal_name").toString();
            String alias = authInfoMap.get("alias").toString();
            String qrcodeUrl = authInfoMap.get("qrcode_url").toString();

            //新增授权记录
            srvWechatMainInfo = new SrvWechatMainInfo();
            srvWechatMainInfo.setId(GUIDUtil.getGUID());
            srvWechatMainInfo.setAuthorizerAppid(authorizerAppid);
            srvWechatMainInfo.setAuthorizerRefreshToken(authorizerRefreshToken);
            srvWechatMainInfo.setCreateDt(DateUtil.getNow());
            srvWechatMainInfo.setSysAccount(authorizerAppid);
            srvWechatMainInfo.setCreateBy("system");
            srvWechatMainInfo.setVerifyTypeInfo(verifyTypeInfoId);
            srvWechatMainInfo.setServiceTypeInfo(serviceTypeInfoId);
            srvWechatMainInfo.setUserName(userName);
            srvWechatMainInfo.setNickName(nickName);
            srvWechatMainInfo.setAlias(alias);
            srvWechatMainInfo.setHeadImg(headUrl);
            srvWechatMainInfo.setPrincipalName(principalName);
            srvWechatMainInfo.setQrcodeUrl(qrcodeUrl);
            srvWechatMainInfoMapper.insertSelective(srvWechatMainInfo);
        }
        //新增授权明细
        for (Map<String, Object> funcInfoItem : funcInfo) {
            Map<String, Integer> funcscopeCategory = (Map<String, Integer>) funcInfoItem.get("funcscope_category");
            Integer id = funcscopeCategory.get("id");
            SrvWechatFuncInfo srvWechatFuncInfo = new SrvWechatFuncInfo();
            srvWechatFuncInfo.setId(GUIDUtil.getGUID());
            srvWechatFuncInfo.setSysAccount(authorizerAppid);
            srvWechatFuncInfo.setCreateBy("system");
            srvWechatFuncInfo.setFuncId(id);
            srvWechatFuncInfo.setMainId(srvWechatMainInfo.getId());
            srvWechatFuncInfo.setCreateDt(DateUtil.getNow());
            srvWechatFuncInfoMapper.insertSelective(srvWechatFuncInfo);
        }
    }
}
