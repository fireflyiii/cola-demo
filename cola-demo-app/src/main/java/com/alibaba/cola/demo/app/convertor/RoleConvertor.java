package com.alibaba.cola.demo.app.convertor;

import com.alibaba.cola.demo.client.dto.data.RoleDTO;
import com.alibaba.cola.demo.domain.user.Role;

/**
 * 角色DTO转换器
 */
public class RoleConvertor {

    private RoleConvertor() {}

    public static RoleDTO toDTO(Role role) {
        if (role == null) {
            return null;
        }
        return RoleDTO.builder()
                .roleId(role.getRoleId())
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .status(role.getStatus())
                .build();
    }
}
