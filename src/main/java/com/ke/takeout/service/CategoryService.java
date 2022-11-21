package com.ke.takeout.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ke.takeout.pojo.Category;


public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
