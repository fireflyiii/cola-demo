package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.client.api.IOrderService;
import com.alibaba.cola.demo.client.dto.OrderAddCmd;
import com.alibaba.cola.demo.client.dto.OrderListByNameQry;
import com.alibaba.cola.demo.client.dto.data.OrderDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    /**
     * 添加订单
     */
    @PostMapping("/add")
    public Response addOrder(@RequestBody @Valid OrderAddCmd cmd) {
        log.info("添加订单请求: orderName={}", cmd.getOrderName());
        return orderService.addOrder(cmd);
    }

    /**
     * 根据名称查询订单列表
     */
    @GetMapping("/list")
    public MultiResponse<OrderDTO> listByName(@RequestParam String orderName) {
        log.info("查询订单列表请求: orderName={}", orderName);
        OrderListByNameQry qry = new OrderListByNameQry();
        qry.setOrderName(orderName);
        return orderService.listByName(qry);
    }
}
