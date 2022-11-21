package com.ke.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ke.takeout.common.R;
import com.ke.takeout.dto.OrdersDto;
import com.ke.takeout.pojo.*;
import com.ke.takeout.service.OrderDetailService;
import com.ke.takeout.service.OrdersService;
import com.ke.takeout.service.ShoppingCartService;
import com.ke.takeout.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order, HttpSession session) {
        ordersService.submit(order, (long) session.getAttribute("user"));
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize) {
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        Page<OrdersDto> res = help(page, pageSize, lambdaQueryWrapper);

        return R.success(res);
    }

    /**
     * 再来一单
     * 由于每次用的是orderdetati的id，所以不能重复再来一单
     * @return
     */
    @PostMapping("/userPage")
    public R<String> userPage1(@RequestBody Map map, HttpSession session) {
        long orderId = Long.valueOf((String) map.get("id"));
        LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OrderDetail::getOrderId, orderId);
        List<ShoppingCart> list = orderDetailService.list(lambdaQueryWrapper).stream().map((item) -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(item, shoppingCart);
            shoppingCart.setUserId((long) session.getAttribute("user"));
            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartService.saveBatch(list);
        return R.success("成功");
    }

    @GetMapping("/page")
    @Transactional
    public R<Page> page(int page, int pageSize, String number, @RequestParam(value = "beginTime", required = false) String start, @RequestParam(value = "endTime", required = false) String end) {
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(number != null ,Orders::getNumber,number);
        lambdaQueryWrapper.between(start != null && end != null,Orders::getOrderTime,start,end);
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        Page<OrdersDto> res = help(page, pageSize, lambdaQueryWrapper);

        return R.success(res);
    }

    @PutMapping
    public R<String> update(@RequestBody Map map) {
        long orderId = Long.valueOf((String) map.get("id"));
        int status = (int) map.get("status");
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getNumber, orderId);
        Orders order = ordersService.getOne(lambdaQueryWrapper);
        order.setStatus(status);
        ordersService.updateById(order);
        return R.success("修改成功");
    }

    private Page<OrdersDto> help(int page, int pageSize, LambdaQueryWrapper<Orders> lambdaQueryWrapper){
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> orderDto = new Page<>();

        ordersService.page(pageInfo, lambdaQueryWrapper);

        BeanUtils.copyProperties(pageInfo, orderDto, "records");
        List<OrdersDto> list = pageInfo.getRecords().stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail::getOrderId, item.getId());
            ordersDto.setOrderDetails(orderDetailService.list(queryWrapper));

            return ordersDto;
        }).collect(Collectors.toList());
        orderDto.setRecords(list);
        return orderDto;
    }

    @GetMapping("/getToDayOrder")
    public R<Long> getToDayOrder(){
        return ordersService.countToDayOrder();
    }

    @GetMapping("/getYesDayOrder")
    public R<Long> getYesDayOrder(){
        return ordersService.countYesDayOrder();
    }

    @GetMapping("/getOneWeekLiuShui")
    public R<Map> getOneWeekLiuShui(){
        return ordersService.OneWeekLiuShui();
    }

    @GetMapping("/getOneWeekOrder")
    public R<Map> getOneWeekOrder(){
        return ordersService.OneWeekOrder();
    }

    @GetMapping("/getHotSeal")
    public R<Map> getHotSeal(){
        return ordersService.hotSeal();
    }

}
