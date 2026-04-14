package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.client.dto.NotificationSendCmd;
import com.alibaba.cola.demo.domain.notification.gateway.NotificationGateway;
import com.alibaba.cola.demo.infrastructure.feign.NotificationFeignClient;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 通知网关实现
 * 通过 Feign Client 远程调用 notification-service
 * Feign + Sentinel 集成，失败时自动触发降级
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationGatewayImpl implements NotificationGateway {

    private final NotificationFeignClient notificationFeignClient;

    @Override
    public boolean sendOrderCreatedNotification(Long orderId, String customerName, BigDecimal amount) {
        NotificationSendCmd cmd = new NotificationSendCmd();
        cmd.setOrderId(orderId);
        cmd.setCustomerName(customerName);
        cmd.setAmount(amount);

        try {
            Response response = notificationFeignClient.sendOrderCreatedNotification(cmd);
            if (response.isSuccess()) {
                log.info("Order created notification sent successfully, orderId: {}", orderId);
                return true;
            } else {
                log.warn("Order created notification failed, orderId: {}, errCode: {}, errMessage: {}",
                        orderId, response.getErrCode(), response.getErrMessage());
                return false;
            }
        } catch (Exception e) {
            // FallbackFactory 已处理降级，此处捕获的是非 Sentinel 场景的异常
            log.error("Unexpected error when sending order notification, orderId: {}", orderId, e);
            return false;
        }
    }
}
