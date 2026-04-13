package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限分页查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionPageQry extends PageQuery {

    /**
     * 权限名称（模糊查询）
     */
    private String permissionName;

    /**
     * 资源类型（精确查询）
     */
    private String resourceType;

    /**
     * 资源路径（模糊查询）
     */
    private String resourcePath;
}
