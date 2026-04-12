package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.domain.user.Permission;
import com.alibaba.cola.demo.infrastructure.dataobject.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * 权限装配器（Entity <-> Domain）
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionAssembler {

    /**
     * Domain转Entity（自动生成，不映射id，由数据库生成）
     */
    PermissionEntity toEntity(Permission permission);

    /**
     * Entity转Domain（使用工厂方法，保留领域校验）
     */
    default Permission toDomain(PermissionEntity entity) {
        if (entity == null) {
            return null;
        }
        Permission permission = Permission.create(
                entity.getPermissionCode(), entity.getPermissionName(),
                entity.getResourceType(), entity.getResourcePath());
        permission.setPermissionId(entity.getId());
        return permission;
    }
}
