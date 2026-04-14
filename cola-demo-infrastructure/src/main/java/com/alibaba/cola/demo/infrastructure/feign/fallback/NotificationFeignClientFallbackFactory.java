package com.alibaba.cola.demo.infrastructure.feign.fallback;

import com.alibaba.cola.demo.client.dto.NotificationSendCmd;
import com.alibaba.cola.demo.infrastructure.feign.NotificationFeignClient;
import com.alibaba.cola.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 通知服务 Feign 客户端降级工厂
 * 当远程调用失败（超时、异常、熔断）时触发降级逻辑
 * 使用 FallbackFactory 可以获取到具体的异常原因，便于日志记录和问题排查
 */
@Slf4j
@Component
public class NotificationFeignClientFallbackFactory implements FallbackFactory<NotificationFeignClient> {

    @Override
    public NotificationFeignClient create(Throwable cause) {
        log.warn("Notification service invocation failed, triggering fallback. reason: {}", cause.getMessage());

        return new NotificationFeignClient() {
            @Override
            public Response sendOrderCreatedNotification(NotificationSendCmd cmd) {
                log.error("Failed to send order created notification, orderId: {}, customerName: {}",
                        cmd.getOrderId(), cmd.getCustomerName(), cause);
                // 降级策略：返回失败响应，由调用方决定后续处理
                // 如果是关键业务可考虑记录到本地表异步重试
                return Response.buildFailure("FEIGN_FALLBACK",
                        "Notification service unavailable: " + cause.getMessage());
            }
        };
    }
}
