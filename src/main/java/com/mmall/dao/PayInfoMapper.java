package com.mmall.dao;

import com.mmall.pojo.PayInfo;

public interface PayInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PayInfo row);

    int insertSelective(PayInfo row);

    PayInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PayInfo row);

    int updateByPrimaryKey(PayInfo row);
}