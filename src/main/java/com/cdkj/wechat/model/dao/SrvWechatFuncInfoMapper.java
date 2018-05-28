package com.cdkj.wechat.model.dao;

import com.cdkj.wechat.model.pojo.SrvWechatFuncInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface SrvWechatFuncInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(SrvWechatFuncInfo record);

    int insertSelective(SrvWechatFuncInfo record);

    SrvWechatFuncInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SrvWechatFuncInfo record);

    int updateByPrimaryKey(SrvWechatFuncInfo record);

    List<SrvWechatFuncInfo> selectByMainId(String id);

    int deleteByMainId(String id);
}
