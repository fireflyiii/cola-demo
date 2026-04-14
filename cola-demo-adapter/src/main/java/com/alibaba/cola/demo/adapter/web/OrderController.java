package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.client.api.IOrderService;
import com.alibaba.cola.demo.client.common.OperationLog;
import com.alibaba.cola.demo.client.dto.OrderAddCmd;
import com.alibaba.cola.demo.client.dto.OrderListByNameQry;
import com.alibaba.cola.demo.client.dto.data.OrderDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    /**
     * 添加订单
     */
    @PostMapping("/add")
    @OperationLog(type = "新增", description = "添加订单")
    public Response addOrder(@RequestBody @Valid OrderAddCmd cmd) {
        return orderService.addOrder(cmd);
    }

    /**
     * 根据名称查询订单列表
     */
    @GetMapping("/list")
    public MultiResponse<OrderDTO> listByName(@RequestParam String orderName) {
        OrderListByNameQry qry = new OrderListByNameQry();
        qry.setOrderName(orderName);
        return orderService.listByName(qry);
    }
}
