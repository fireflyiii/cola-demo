package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.domain.user.Role;
import com.alibaba.cola.demo.infrastructure.dataobject.RoleEntity;

/**
 * 角色转换器（Entity <-> Domain）
 */
public class RoleConvertor {

    private RoleConvertor() {}

    public static Role toDomain(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        Role role = Role.create(entity.getRoleCode(), entity.getRoleName());
        role.setRoleId(entity.getId());
        return role;
    }

    public static RoleEntity toEntity(Role role) {
        if (role == null) {
            return null;
        }
        RoleEntity entity = new RoleEntity();
        entity.setId(role.getRoleId());
        entity.setRoleCode(role.getRoleCode());
        entity.setRoleName(role.getRoleName());
        return entity;
    }
}
