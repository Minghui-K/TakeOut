package com.ke.takeout.dto;


import com.ke.takeout.pojo.OrderDetail;
import com.ke.takeout.pojo.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private List<OrderDetail> orderDetails;
	
}
