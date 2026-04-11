package com.alibaba.cola.demo.app.convertor;

import com.alibaba.cola.demo.client.dto.data.PermissionDTO;
import com.alibaba.cola.demo.domain.user.Permission;

/**
 * 权限DTO转换器
 */
public class PermissionConvertor {

    private PermissionConvertor() {}

    public static PermissionDTO toDTO(Permission permission) {
        if (permission == null) {
            return null;
        }
        return PermissionDTO.builder()
                .permissionId(permission.getPermissionId())
                .permissionCode(permission.getPermissionCode())
                .permissionName(permission.getPermissionName())
                .resourceType(permission.getResourceType())
                .resourcePath(permission.getResourcePath())
                .build();
    }
}
