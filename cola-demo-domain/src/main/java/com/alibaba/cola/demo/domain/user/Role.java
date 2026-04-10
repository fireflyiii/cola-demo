package com.alibaba.cola.demo.domain.user;

import lombok.Data;

import java.util.List;

/**
 * 角色实体
 */
@Data
public class Role {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private Integer status;
    private List<Permission> permissions;

    /**
     * 判断角色是否启用
     */
    public boolean isEnabled() {
        return this.status != null && this.status == 1;
    }
}
