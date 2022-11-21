package com.ke.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ke.takeout.common.CustomException;
import com.ke.takeout.mapper.CategoryMapper;
import com.ke.takeout.pojo.Category;
import com.ke.takeout.pojo.Dish;
import com.ke.takeout.pojo.Setmeal;
import com.ke.takeout.service.CategoryService;
import com.ke.takeout.service.DishService;
import com.ke.takeout.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceimpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查询当前分类是否关联菜品
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(dishLambdaQueryWrapper);

        if (count > 0) {
            throw new CustomException("当前分类项关联菜品，无法删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查询当前分类是否关联套餐
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        count = dishService.count(dishLambdaQueryWrapper);
        if (count > 0) {
            throw new CustomException("当前分类项关联套餐，无法删除");
        }


        super.removeById(id);
    }
}
