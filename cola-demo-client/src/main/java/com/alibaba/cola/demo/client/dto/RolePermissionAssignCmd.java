package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

/**
 * 为角色分配权限命令
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RolePermissionAssignCmd extends Command {

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @NotNull(message = "权限ID不能为空")
    private Long permissionId;
}
