package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.domain.user.Permission;
import com.alibaba.cola.demo.domain.enums.ResourceType;
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
     * Entity转Domain（使用rebuild方法，从DB加载不触发创建校验）
     */
    default Permission toDomain(PermissionEntity entity) {
        if (entity == null) {
            return null;
        }
        return Permission.rebuild(entity.getId(), entity.getPermissionCode(), entity.getPermissionName(),
                entity.getResourceType() != null ? ResourceType.valueOf(entity.getResourceType()) : ResourceType.API,
                entity.getResourcePath());
    }
}
