package com.ke.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ke.takeout.common.R;
import com.ke.takeout.pojo.Orders;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface OrdersService extends IService<Orders> {
    void submit(Orders order, long userId);

    R<Map> hotSeal();

    R<Map> OneWeekOrder();

    R<Map> OneWeekLiuShui();

    R<Long> countYesDayOrder();

    R<Long> countToDayOrder();
}
