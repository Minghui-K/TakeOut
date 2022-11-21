package com.ke.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ke.takeout.common.R;
import com.ke.takeout.pojo.OrderDetail;
import com.ke.takeout.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailcontroller {

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/{orderId}")
    public R<List<OrderDetail>> list(@PathVariable long orderId) {
        LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> list = orderDetailService.list(lambdaQueryWrapper);
        return R.success(list);
    }
}
