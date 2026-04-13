package com.alibaba.cola.demo.domain.order;

import com.alibaba.cola.demo.client.common.BizErrorCode;
import com.alibaba.cola.demo.client.common.DomainException;
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
    private OrderStatus status;

    /**
     * 创建订单
     */
    public static Order create(String orderName, BigDecimal amount, String customerName) {
        Order order = new Order();
        order.orderName = orderName;
        order.amount = amount;
        order.customerName = customerName;
        order.status = OrderStatus.CREATED;
        order.validate();
        return order;
    }

    /**
     * 重建订单实体（由Assembler从持久化层加载时使用）
     */
    public static Order rebuild(Long orderId, String orderName, BigDecimal amount,
                                String customerName, OrderStatus status) {
        Order order = new Order();
        order.orderId = orderId;
        order.orderName = orderName;
        order.amount = amount;
        order.customerName = customerName;
        order.status = status;
        return order;
    }

    /**
     * 领域行为：校验订单信息
     */
    public void validate() {
        if (orderName == null || orderName.trim().isEmpty()) {
            throw new DomainException(BizErrorCode.B_ORDER_NAME_NOT_BLANK);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException(BizErrorCode.B_ORDER_AMOUNT_INVALID);
        }
    }

    /**
     * 设置订单ID（由Gateway在持久化后回填主键）
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
