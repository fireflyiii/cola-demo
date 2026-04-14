package com.alibaba.cola.demo.infrastructure.messaging;

import com.alibaba.cola.demo.domain.common.MessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * NoOp 消息发布实现
 * 当 RocketMQ 不可用时作为降级实现，仅记录日志不实际发送消息
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "rocketmq.enabled", havingValue = "false")
public class NoOpMessagePublisher implements MessagePublisher {

    @Override
    public void publish(String topic, String tag, Object message) {
        log.debug("Message publishing skipped (RocketMQ disabled): topic={}, tag={}, message={}",
                topic, tag, message.getClass().getSimpleName());
    }
}
