package com.ke.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ke.takeout.dto.SetmealDto;
import com.ke.takeout.pojo.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SetmealService extends IService<Setmeal> {

    void saveWithDish(SetmealDto setmealDto);
    SetmealDto getWithDish(Long id);
    void updateWithDish(SetmealDto setmealDto);
    List<Setmeal> removeWithDish(List<Long> ids);
}
