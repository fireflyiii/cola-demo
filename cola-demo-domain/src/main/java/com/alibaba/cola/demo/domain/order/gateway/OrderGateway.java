package com.alibaba.cola.demo.domain.order.gateway;

import com.alibaba.cola.demo.domain.order.Order;

import java.util.List;

public interface OrderGateway {

    void create(Order order);

    List<Order> listByName(String orderName);
}
