package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderListByNameQry extends Query {

    private String orderName;
}
