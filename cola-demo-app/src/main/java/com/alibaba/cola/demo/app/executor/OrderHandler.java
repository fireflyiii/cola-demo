package com.alibaba.cola.demo.app.executor;

import com.alibaba.cola.demo.app.convertor.OrderConvertor;
import com.alibaba.cola.demo.client.dto.OrderAddCmd;
import com.alibaba.cola.demo.client.dto.OrderListByNameQry;
import com.alibaba.cola.demo.client.dto.data.OrderDTO;
import com.alibaba.cola.demo.domain.order.Order;
import com.alibaba.cola.demo.domain.order.gateway.OrderGateway;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderHandler {

    private final OrderGateway orderGateway;
    private final OrderConvertor orderConvertor;

    @Transactional(rollbackFor = Exception.class)
    public Response add(OrderAddCmd cmd) {
        Order order = Order.create(cmd.getOrderName(), cmd.getAmount(), cmd.getCustomerName());
        orderGateway.create(order);
        return Response.buildSuccess();
    }

    @Transactional(readOnly = true)
    public MultiResponse<OrderDTO> listByName(OrderListByNameQry qry) {
        List<Order> orders = orderGateway.listByName(qry.getOrderName());
        List<OrderDTO> dtos = orders.stream()
                .map(orderConvertor::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtos);
    }
}
