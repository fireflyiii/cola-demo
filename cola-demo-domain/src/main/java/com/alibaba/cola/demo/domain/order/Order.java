package com.alibaba.cola.demo.domain.order;

import com.alibaba.cola.demo.domain.common.AggregateRoot;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 订单聚合根
 */
@Getter
public class Order implements AggregateRoot {

    private Long orderId;
    private String orderName;
    private BigDecimal amount;
    private String customerName;
    private String status;

    /**
     * 创建订单
     */
    public static Order create(String orderName, BigDecimal amount, String customerName) {
        Order order = new Order();
        order.orderName = orderName;
        order.amount = amount;
        order.customerName = customerName;
        order.status = "CREATED";
        return order;
    }

    /**
     * 设置订单ID（由Gateway在持久化后调用）
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 设置订单状态（由Assembler从持久化重建时调用）
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
