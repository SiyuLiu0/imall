package com.mmall.dao;

import com.mmall.pojo.Shipping;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping row);

    int insertSelective(Shipping row);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping row);

    int updateByPrimaryKey(Shipping row);
}