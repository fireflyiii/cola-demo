package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderAddCmd extends Command {

    @NotBlank(message = "订单名称不能为空")
    private String orderName;

    @NotNull(message = "订单金额不能为空")
    private BigDecimal amount;

    @NotBlank(message = "客户名称不能为空")
    private String customerName;
}
