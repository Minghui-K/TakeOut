package com.ke.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ke.takeout.common.R;
import com.ke.takeout.pojo.Dish;
import com.ke.takeout.pojo.ShoppingCart;
import com.ke.takeout.service.DishService;
import com.ke.takeout.service.SetmealService;
import com.ke.takeout.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpSession session) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, session.getAttribute("user"));
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart, HttpSession session) {
        ShoppingCart res = check(shoppingCart, session);
        if (res != null) {
            // 存在就+1
            res.setNumber(res.getNumber() + 1);
            shoppingCartService.updateById(res);
        } else {
            // 不存在直接添加
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
        }
        return R.success("添加成功");
    }

    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart, HttpSession session) {
        ShoppingCart res = check(shoppingCart, session);
        if (res != null) {
            // 存在就-1
            if (res.getNumber() == 1) {
                shoppingCartService.removeById(res);
            } else {
                res.setNumber(res.getNumber() - 1);
                shoppingCartService.updateById(res);
            }
            return R.success("删除成功");
        } else {
            // 不存在错误
            return R.error("删除失败");
        }
    }

    private ShoppingCart check(ShoppingCart shoppingCart, HttpSession session) {
        shoppingCart.setUserId((long) session.getAttribute("user"));
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        //lambdaQueryWrapper.eq(shoppingCart.getDishFlavor() != null, ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());

        //确认是否已在购物车中, 分菜品和套餐
        if (shoppingCart.getDishId() != null) {
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        return shoppingCartService.getOne(lambdaQueryWrapper);
    }

    @DeleteMapping ("/clean")
    public R<String> clean(HttpSession session) {
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, session.getAttribute("user"));
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("清空成功");
    }
}
