package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.domain.user.Permission;
import com.alibaba.cola.demo.infrastructure.dataobject.PermissionEntity;

/**
 * 权限转换器（Entity <-> Domain）
 */
public class PermissionConvertor {

    private PermissionConvertor() {}

    public static Permission toDomain(PermissionEntity entity) {
        if (entity == null) {
            return null;
        }
        Permission permission = Permission.create(
                entity.getPermissionCode(),
                entity.getPermissionName(),
                entity.getResourceType(),
                entity.getResourcePath()
        );
        permission.setPermissionId(entity.getId());
        return permission;
    }

    public static PermissionEntity toEntity(Permission permission) {
        if (permission == null) {
            return null;
        }
        PermissionEntity entity = new PermissionEntity();
        entity.setId(permission.getPermissionId());
        entity.setPermissionCode(permission.getPermissionCode());
        entity.setPermissionName(permission.getPermissionName());
        entity.setResourceType(permission.getResourceType());
        entity.setResourcePath(permission.getResourcePath());
        return entity;
    }
}
