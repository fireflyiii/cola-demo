package com.alibaba.cola.demo.domain.common;

/**
 * 消息发布接口
 * 领域层定义接口，基础设施层提供实现
 * 用于跨服务的领域事件传播
 */
public interface MessagePublisher {

    /**
     * 发布消息到指定Topic
     *
     * @param topic   消息Topic
     * @param tag     消息Tag（可为null）
     * @param message 消息内容
     */
    void publish(String topic, String tag, Object message);

    /**
     * 发布消息到指定Topic（无Tag）
     *
     * @param topic   消息Topic
     * @param message 消息内容
     */
    default void publish(String topic, Object message) {
        publish(topic, null, message);
    }
}
