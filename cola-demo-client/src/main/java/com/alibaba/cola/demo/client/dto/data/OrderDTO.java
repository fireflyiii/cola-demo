package com.alibaba.cola.demo.client.dto.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDTO extends DTO {

    private Long orderId;
    private String orderName;
    private BigDecimal amount;
    private String customerName;
    private String status;
}
