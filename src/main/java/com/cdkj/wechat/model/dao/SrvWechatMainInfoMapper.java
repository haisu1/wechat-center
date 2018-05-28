package com.cdkj.wechat.model.dao;

import com.cdkj.wechat.model.pojo.SrvWechatMainInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SrvWechatMainInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(SrvWechatMainInfo record);

    int insertSelective(SrvWechatMainInfo record);

    SrvWechatMainInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SrvWechatMainInfo record);

    int updateByPrimaryKey(SrvWechatMainInfo record);

    SrvWechatMainInfo selectByAuthAppId(String authorizerAppid);
}
