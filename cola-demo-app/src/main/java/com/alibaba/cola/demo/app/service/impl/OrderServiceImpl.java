package com.alibaba.cola.demo.app.service.impl;

import com.alibaba.cola.demo.app.executor.OrderHandler;
import com.alibaba.cola.demo.client.api.IOrderService;
import com.alibaba.cola.demo.client.dto.OrderAddCmd;
import com.alibaba.cola.demo.client.dto.OrderListByNameQry;
import com.alibaba.cola.demo.client.dto.data.OrderDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderHandler orderHandler;

    @Override
    public Response addOrder(OrderAddCmd cmd) {
        return orderHandler.add(cmd);
    }

    @Override
    public MultiResponse<OrderDTO> listByName(OrderListByNameQry qry) {
        return orderHandler.listByName(qry);
    }
}
