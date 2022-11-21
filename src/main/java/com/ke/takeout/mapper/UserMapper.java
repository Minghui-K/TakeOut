package com.ke.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ke.takeout.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
