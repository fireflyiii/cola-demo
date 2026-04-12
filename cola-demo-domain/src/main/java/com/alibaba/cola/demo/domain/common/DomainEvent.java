package com.alibaba.cola.demo.domain.common;

import java.time.LocalDateTime;

/**
 * 领域事件基类
 * 所有领域事件都应继承此类
 */
public abstract class DomainEvent {

    private final LocalDateTime occurredOn;

    protected DomainEvent() {
        this.occurredOn = LocalDateTime.now();
    }

    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
