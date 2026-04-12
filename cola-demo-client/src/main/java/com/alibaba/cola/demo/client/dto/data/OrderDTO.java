package com.alibaba.cola.demo.client.dto.data;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDTO {

    private Long orderId;
    private String orderName;
    private BigDecimal amount;
    private String customerName;
    private String status;
}
