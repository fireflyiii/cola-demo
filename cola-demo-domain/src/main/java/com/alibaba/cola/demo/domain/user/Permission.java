package com.alibaba.cola.demo.domain.user;

import lombok.Getter;

/**
 * 权限实体
 */
@Getter
public class Permission {
    private Long permissionId;
    private String permissionCode;
    private String permissionName;
    private String resourceType;
    private String resourcePath;

    /**
     * 创建权限
     */
    public static Permission create(String permissionCode, String permissionName, String resourceType, String resourcePath) {
        Permission permission = new Permission();
        permission.permissionCode = permissionCode;
        permission.permissionName = permissionName;
        permission.resourceType = resourceType;
        permission.resourcePath = resourcePath;
        return permission;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }
}
