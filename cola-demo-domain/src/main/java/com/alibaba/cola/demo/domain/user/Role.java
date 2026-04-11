package com.alibaba.cola.demo.domain.user;

import lombok.Getter;

import java.util.List;

/**
 * 角色实体
 */
@Getter
public class Role {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private Integer status;
    private List<Permission> permissions;

    /**
     * 创建角色
     */
    public static Role create(String roleCode, String roleName) {
        Role role = new Role();
        role.roleCode = roleCode;
        role.roleName = roleName;
        return role;
    }

    /**
     * 判断角色是否启用
     */
    public boolean isEnabled() {
        return this.status != null && this.status == 1;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
