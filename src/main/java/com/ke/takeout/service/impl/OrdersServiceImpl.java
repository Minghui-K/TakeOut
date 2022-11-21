package com.ke.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ke.takeout.common.CustomException;
import com.ke.takeout.common.R;
import com.ke.takeout.mapper.*;
import com.ke.takeout.pojo.*;
import com.ke.takeout.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private OrdersMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submit(Orders orders, long userId) {

        //查询当前用户购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(queryWrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("购物车为空，不能下单！");
        }
        //查询用户数据
        User user = userMapper.selectById(userId);
        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookMapper.selectById(addressBookId);
        if (addressBook == null ){
            throw new CustomException("地址信息有错误，不能下单！");
        }

        //向订单表插入数据，一条数据
        long orderId = IdWorker.getId();
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        orderMapper.insert(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartMapper.delete(queryWrapper);
    }

    @Override
    public R<Map> hotSeal() {
        Map<String,Object> data = new HashMap<>();

        QueryWrapper<OrderDetail> detailQueryWrapper = new QueryWrapper<>();
        detailQueryWrapper.select("SUM(number) as dishCount,name ").groupBy("name").isNotNull("dish_id").orderByDesc("dishCount").last("LIMIT 2");
        List<Map<String,Object>> orderDetails = orderDetailMapper.selectMaps(detailQueryWrapper);
        data.put("dishCount",orderDetails);

        QueryWrapper<OrderDetail> detailQueryWrapper1 = new QueryWrapper<>();
        detailQueryWrapper1.select("SUM(number) as setmealCount,name ").groupBy("name").isNotNull("setmeal_id").orderByDesc("setmealCount").last("LIMIT 2");
        List<Map<String,Object>> setmealDetails = orderDetailMapper.selectMaps(detailQueryWrapper1);
        data.put("setmealCount",setmealDetails);

        return R.success(data);
    }

    @Override
    public R<Map> OneWeekOrder() {

        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(-7);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).plusDays(-7);
        LocalDate startDay = LocalDate.now().plusDays(-7);

        Map<String,Object> map = new HashMap<>();
        List<Long> orderCount = new ArrayList<>();
        List<LocalDate> days = new ArrayList<>();
        for (int i = 0;i<7;i++){
            LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.between(Orders::getOrderTime,start,end);
            long aLong = orderMapper.selectCount(queryWrapper);
            orderCount.add(aLong);
            start = start.plusDays(1);
            end = end.plusDays(1);
            days.add(startDay);
            startDay = startDay.plusDays(1);
        }
        map.put("orderCount",orderCount);
        map.put("days",days);
        return R.success(map);
    }

    @Override
    public R<Map> OneWeekLiuShui() {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(-7);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).plusDays(-7);
        LocalDate startDay = LocalDate.now().plusDays(-7);

        Map<String,Object> map = new HashMap<>();
        List<Double> amount = new ArrayList<>();
        List<LocalDate> days = new ArrayList<>();
        for (int i = 0;i<7;i++){
            LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.between(Orders::getOrderTime,start,end);
            List<Orders> orders = orderMapper.selectList(queryWrapper);
            double orderAmount = 0;
            for (Orders order : orders) {
                orderAmount += order.getAmount().doubleValue();
            }
            amount.add(orderAmount);
            start = start.plusDays(1);
            end = end.plusDays(1);
            days.add(startDay);
            startDay = startDay.plusDays(1);

        }
        map.put("amount",amount);
        map.put("days",days);
        return R.success(map);
    }

    @Override
    public R<Long> countYesDayOrder() {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(-1);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).plusDays(-1);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Orders::getOrderTime,start,end);
        long aLong = orderMapper.selectCount(queryWrapper);


        return R.success(aLong);
    }

    @Override
    public R<Long> countToDayOrder() {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Orders::getOrderTime,start,end);
        long aLong = orderMapper.selectCount(queryWrapper);
        return R.success(aLong);
    }
}
