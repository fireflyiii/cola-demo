package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 为用户分配角色命令
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserRoleAssignCmd extends Command {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "角色ID不能为空")
    private Long roleId;
}
