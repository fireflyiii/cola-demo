package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户分页查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerPageQry extends PageQuery {

    /**
     * 客户名称（模糊查询）
     */
    private String customerName;
}
