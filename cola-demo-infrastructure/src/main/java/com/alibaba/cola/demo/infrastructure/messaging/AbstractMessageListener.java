package com.alibaba.cola.demo.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQListener;

/**
 * RocketMQ 消费者基类
 * 提供统一的消费模板，包括日志记录和异常处理
 *
 * 用法：
 * <pre>
 * &#64;RocketMQMessageListener(topic = "domain-event:CustomerCreatedEvent", consumerGroup = "my-group")
 * &#64;Component
 * public class CustomerCreatedConsumer extends AbstractMessageListener&lt;CustomerCreatedEvent&gt; {
 *     &#64;Override
 *     protected void handleMessage(CustomerCreatedEvent event) {
 *         // 处理事件
 *     }
 * }
 * </pre>
 */
@Slf4j
public abstract class AbstractMessageListener<T> implements RocketMQListener<T> {

    @Override
    public void onMessage(T message) {
        log.info("Received message: {}", message.getClass().getSimpleName());
        try {
            handleMessage(message);
            log.info("Message processed successfully: {}", message.getClass().getSimpleName());
        } catch (Exception ex) {
            log.error("Failed to process message {}: {}", message.getClass().getSimpleName(), ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * 子类实现具体消息处理逻辑
     */
    protected abstract void handleMessage(T message);
}
