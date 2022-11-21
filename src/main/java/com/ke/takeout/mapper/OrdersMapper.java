package com.ke.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ke.takeout.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
