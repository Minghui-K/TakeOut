package com.ke.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ke.takeout.dto.DishDto;
import com.ke.takeout.pojo.Dish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DishService extends IService<Dish> {
    /**
     * 添加菜品和口味
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);
    DishDto getWithFlavor(Long id);
    void updateWithFlavor(DishDto dishDto);
    void removeWithFlavor(List<Long> ids);
}
