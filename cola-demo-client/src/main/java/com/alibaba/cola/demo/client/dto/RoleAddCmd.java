package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 角色创建命令
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleAddCmd extends Command {

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;
}
