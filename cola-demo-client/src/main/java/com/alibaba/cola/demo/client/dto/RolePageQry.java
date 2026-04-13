package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色分页查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RolePageQry extends PageQuery {

    /**
     * 角色名称（模糊查询）
     */
    private String roleName;

    /**
     * 状态（精确查询）
     */
    private Integer status;
}
