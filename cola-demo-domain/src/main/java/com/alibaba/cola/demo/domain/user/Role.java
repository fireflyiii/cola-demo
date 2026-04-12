package com.alibaba.cola.demo.domain.user;

import com.alibaba.cola.demo.client.common.BizErrorCode;
import com.alibaba.cola.demo.client.common.DomainException;
import com.alibaba.cola.demo.domain.common.AggregateRoot;
import lombok.Getter;

import java.util.List;

/**
 * 角色聚合根
 */
@Getter
public class Role implements AggregateRoot {
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
        role.validate();
        return role;
    }

    /**
     * 领域行为：校验角色信息
     */
    public void validate() {
        if (roleCode == null || roleCode.trim().isEmpty()) {
              throw new DomainException(BizErrorCode.B_ROLE_CODE_NOT_BLANK);
          }
          if (roleName == null || roleName.trim().isEmpty()) {
              throw new DomainException(BizErrorCode.B_ROLE_NAME_NOT_BLANK);
        }
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
