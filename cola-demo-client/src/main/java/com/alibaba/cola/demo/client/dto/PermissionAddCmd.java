package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;

/**
 * 权限创建命令
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionAddCmd extends Command {

    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;

    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    private String resourceType;

    private String resourcePath;
}
