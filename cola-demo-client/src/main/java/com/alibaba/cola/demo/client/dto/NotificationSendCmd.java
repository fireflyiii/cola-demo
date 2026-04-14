package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 发送订单通知命令
 * 用于 Feign 调用通知服务的请求体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationSendCmd extends Command {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    @NotNull(message = "订单金额不能为空")
    private BigDecimal amount;
}
