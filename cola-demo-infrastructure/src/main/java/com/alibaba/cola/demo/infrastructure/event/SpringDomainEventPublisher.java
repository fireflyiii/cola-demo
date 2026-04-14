package com.alibaba.cola.demo.infrastructure.event;

import com.alibaba.cola.demo.domain.common.DomainEvent;
import com.alibaba.cola.demo.domain.common.DomainEventPublisher;
import com.alibaba.cola.demo.domain.common.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 领域事件发布实现（双发模式）
 * 1. 本地事件：通过 Spring ApplicationEvent 在同一 JVM 内传播
 * 2. 远程事件：通过 MessagePublisher (RocketMQ) 跨服务传播
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessagePublisher messagePublisher;

    @Override
    public void publish(DomainEvent event) {
        // 本地事件发布
        applicationEventPublisher.publishEvent(event);
        log.debug("Local event published: {}", event.getClass().getSimpleName());

        // 远程事件发布（通过 RocketMQ）
        String topic = "domain-event:" + event.getClass().getSimpleName();
        try {
            messagePublisher.publish(topic, event);
        } catch (Exception ex) {
            // 远程发布失败不影响本地事务，仅记录日志
            log.warn("Remote event publish failed for {}: {}", event.getClass().getSimpleName(), ex.getMessage());
        }
    }
}
