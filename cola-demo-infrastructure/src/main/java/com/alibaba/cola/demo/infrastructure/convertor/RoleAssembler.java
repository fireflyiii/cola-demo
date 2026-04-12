package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.domain.user.Role;
import com.alibaba.cola.demo.infrastructure.dataobject.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * 角色装配器（Entity <-> Domain）
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleAssembler {

    /**
     * Domain转Entity（自动生成，不映射id，由数据库生成）
     */
    RoleEntity toEntity(Role role);

    /**
     * Entity转Domain（使用工厂方法，保留领域校验）
     */
    default Role toDomain(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        Role role = Role.create(entity.getRoleCode(), entity.getRoleName());
        role.setRoleId(entity.getId());
        return role;
    }
}
