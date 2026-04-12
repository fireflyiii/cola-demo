package com.alibaba.cola.demo.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order")
public class OrderEntity extends BaseEntity {

    private String orderName;
    private BigDecimal amount;
    private String customerName;
    private String status;
}
