package com.alibaba.cola.demo.infrastructure.feign;

import com.alibaba.cola.demo.client.dto.NotificationSendCmd;
import com.alibaba.cola.demo.infrastructure.feign.fallback.NotificationFeignClientFallbackFactory;
import com.alibaba.cola.dto.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

/**
 * 通知服务 Feign 客户端
 * 通过 Eureka 服务发现调用 notification-service
 * 配合 Sentinel 实现熔断降级，降级逻辑由 FallbackFactory 提供
 */
@FeignClient(
        name = "notification-service",
        fallbackFactory = NotificationFeignClientFallbackFactory.class,
        configuration = FeignClientConfig.class
)
public interface NotificationFeignClient {

    /**
     * 发送订单创建通知
     */
    @PostMapping("/api/v1/notification/order-created")
    Response sendOrderCreatedNotification(@RequestBody @Valid NotificationSendCmd cmd);
}
