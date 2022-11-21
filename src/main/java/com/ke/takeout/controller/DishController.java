package com.ke.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ke.takeout.common.R;
import com.ke.takeout.dto.DishDto;
import com.ke.takeout.pojo.Category;
import com.ke.takeout.pojo.Dish;
import com.ke.takeout.pojo.DishFlavor;
import com.ke.takeout.service.CategoryService;
import com.ke.takeout.service.DishFlavorService;
import com.ke.takeout.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")

public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
//    @Autowired
//    private RedisTemplate redisTemplate;

    @CacheEvict(value = "dish", allEntries = true)
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        // 删除缓存防止脏读 多线程需要用锁
//        String key = "dish_category_" + dishDto.getCategoryId() + "*";
//        Set keys = redisTemplate.keys(key);
//        redisTemplate.delete(keys);
        return R.success("添加成功！");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        // 先分页查询
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Dish::getName, name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, lambdaQueryWrapper);
        // 复制一个除了records，使用DishDto因为需要CategoryName
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        // 根据原records把每个id查表得到name换上
        List<DishDto> list = pageInfo.getRecords().stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            // 放入CategoryName
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());
        // 然后把得到的有名字的records放入。
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getWithFlavor(id);
        return R.success(dishDto);
    }

    @CacheEvict(value = "dish", allEntries = true)
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        // 删除缓存防止脏读 多线程需要用锁
//        String key = "dish_category_" + dishDto.getCategoryId() + "*";
//        Set keys = redisTemplate.keys(key);
//        redisTemplate.delete(keys);
        return R.success("修改成功！");
    }

    @CacheEvict(value = "dish", allEntries = true)
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> id) {
        List<Dish> list = dishService.removeWithFlavor(id);
        // 删除缓存
//        for (Dish dish : list) {
//            String key = "dish_category_" + dish.getCategoryId() + "_status_" + dish.getStatus();
//            Set keys = redisTemplate.keys(key);
//            redisTemplate.delete(keys);
//        }
        return R.success("删除成功！");
    }

    @CacheEvict(value = "dish", allEntries = true)
    @PostMapping("status/{to}")
    public R<String> status(@PathVariable int to, @RequestParam List<Long> id) {
        String key = null;
        for (long i : id) {
            Dish dish = dishService.getById(i);
            dish.setStatus(to);
            dishService.updateById(dish);
            key = "dish_category_" + dish.getCategoryId() + "*";
        }
//        // 删除缓存防止脏读 多线程需要用锁
//        Set keys = redisTemplate.keys(key);
//        redisTemplate.delete(keys);
        return R.success("修改成功!");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
//        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus, 1);//只要在售的
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }

    @Cacheable(value = "dish", key = "'dish_category_'+#dish.categoryId")
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        // 添加redis作为缓存防止过多访问数据库
        List<DishDto> list = null;
//        String key = "dish_category_" + dish.getCategoryId() + "_status_" + dish.getStatus();
//        list = (List<DishDto>) redisTemplate.opsForValue().get(key);
//        if (list != null) {
//            return R.success(list);
//        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);//只要在售的
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        list = dishService.list(queryWrapper).stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            // 放入CategoryName
            dishDto.setCategoryName(category.getName());

            // 放入Flavors
            long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            dishDto.setFlavors(dishFlavorService.list(lambdaQueryWrapper));

            return dishDto;
        }).collect(Collectors.toList());
        // 存入redis
//        redisTemplate.opsForValue().set(key, list, 24, TimeUnit.HOURS);
        return R.success(list);
    }
}
