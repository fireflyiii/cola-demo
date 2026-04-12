package com.alibaba.cola.demo.domain.customer;

import com.alibaba.cola.demo.domain.common.DomainEvent;
import lombok.Getter;

/**
 * 客户已创建事件
 */
@Getter
public class CustomerCreatedEvent extends DomainEvent {

    private final Long customerId;
    private final String customerName;

    public CustomerCreatedEvent(Long customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }
}
