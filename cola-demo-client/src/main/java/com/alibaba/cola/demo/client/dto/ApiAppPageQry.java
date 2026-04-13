package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * API应用分页查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiAppPageQry extends PageQuery {

    /**
     * 应用名称（模糊查询）
     */
    private String appName;

    /**
     * 状态（精确查询）
     */
    private Integer status;
}
