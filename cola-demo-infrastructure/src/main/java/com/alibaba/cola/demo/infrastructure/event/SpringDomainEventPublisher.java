package com.alibaba.cola.demo.infrastructure.event;

import com.alibaba.cola.demo.domain.common.DomainEvent;
import com.alibaba.cola.demo.domain.common.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 基于Spring ApplicationEvent的领域事件发布实现
 */
@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
