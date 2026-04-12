package com.alibaba.cola.demo.domain.common;

/**
 * 领域事件发布接口
 * 领域层定义接口，基础设施层提供实现
 */
public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
