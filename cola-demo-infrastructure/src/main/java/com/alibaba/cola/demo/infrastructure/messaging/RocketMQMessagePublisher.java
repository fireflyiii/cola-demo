package com.alibaba.cola.demo.infrastructure.messaging;

import com.alibaba.cola.demo.domain.common.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * RocketMQ 消息发布实现
 * 基于 rocketmq-spring-boot-starter
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(RocketMQTemplate.class)
public class RocketMQMessagePublisher implements MessagePublisher {

    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public void publish(String topic, String tag, Object message) {
        String destination = tag != null ? topic + ":" + tag : topic;
        try {
            rocketMQTemplate.syncSend(destination, message);
            log.info("Message published to {}: {}", destination, message.getClass().getSimpleName());
        } catch (Exception ex) {
            log.error("Failed to publish message to {}: {}", destination, ex.getMessage(), ex);
            throw new RuntimeException("Message publish failed: " + destination, ex);
        }
    }
}
