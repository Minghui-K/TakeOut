package com.ke.takeout.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ke.takeout.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
