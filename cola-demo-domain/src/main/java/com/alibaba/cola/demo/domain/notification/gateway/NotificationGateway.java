package com.alibaba.cola.demo.domain.notification.gateway;

import java.math.BigDecimal;

/**
 * 通知网关接口
 * 领域层定义接口，基础设施层通过 Feign 远程调用实现
 * 示例：创建订单后通知通知服务发送消息
 */
public interface NotificationGateway {

    /**
     * 发送订单创建通知
     *
     * @param orderId      订单ID
     * @param customerName 客户名称
     * @param amount       订单金额
     * @return 是否发送成功
     */
    boolean sendOrderCreatedNotification(Long orderId, String customerName, BigDecimal amount);
}
