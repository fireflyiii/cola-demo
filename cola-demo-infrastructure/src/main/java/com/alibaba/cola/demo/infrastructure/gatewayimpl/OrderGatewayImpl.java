package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.domain.order.Order;
import com.alibaba.cola.demo.domain.order.gateway.OrderGateway;
import com.alibaba.cola.demo.infrastructure.convertor.OrderAssembler;
import com.alibaba.cola.demo.infrastructure.dataobject.OrderEntity;
import com.alibaba.cola.demo.infrastructure.mapper.OrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单网关实现
 */
@Component
@RequiredArgsConstructor
public class OrderGatewayImpl implements OrderGateway {

    private final OrderMapper orderMapper;
    private final OrderAssembler orderAssembler;

    @Override
    public void create(Order order) {
        OrderEntity orderEntity = orderAssembler.toEntity(order);
        orderMapper.insert(orderEntity);
        order.setOrderId(orderEntity.getId());
    }

    @Override
    public List<Order> listByName(String orderName) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(OrderEntity::getOrderName, orderName);
        return orderMapper.selectList(wrapper).stream()
                .map(orderAssembler::toDomain)
                .collect(Collectors.toList());
    }
}
