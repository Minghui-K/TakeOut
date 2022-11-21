package com.ke.takeout.dto;

import com.ke.takeout.pojo.Setmeal;
import com.ke.takeout.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
