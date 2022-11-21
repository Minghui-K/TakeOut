package com.ke.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ke.takeout.common.R;
import com.ke.takeout.dto.DishDto;
import com.ke.takeout.dto.SetmealDto;
import com.ke.takeout.pojo.Category;
import com.ke.takeout.pojo.Dish;
import com.ke.takeout.pojo.Setmeal;
import com.ke.takeout.service.CategoryService;
import com.ke.takeout.service.SetmealDishService;
import com.ke.takeout.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功！");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        // 先分页查询
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, lambdaQueryWrapper);
        // 复制一个除了records，使用DishDto因为需要CategoryName
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        // 根据原records把每个id查表得到name换上
        List<SetmealDto> list = pageInfo.getRecords().stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            // 放入CategoryName
            setmealDto.setCategoryName(category.getName());

            return setmealDto;
        }).collect(Collectors.toList());
        // 然后把得到的有名字的records放入。
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功！");
    }

    @PostMapping("status/{to}")
    public R<String> status(@PathVariable int to, @RequestParam List<Long> id) {
        for (long i : id) {
            Setmeal setmeal = setmealService.getById(i);
            setmeal.setStatus(to);
            setmealService.updateById(setmeal);
        }
        return R.success("修改成功!");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> id) {
        setmealService.removeWithDish(id);
        return R.success("删除成功！");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus, setmeal.getStatus());
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

}
