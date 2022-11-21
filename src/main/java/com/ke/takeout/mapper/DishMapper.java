package com.ke.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ke.takeout.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
