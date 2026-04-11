package com.alibaba.cola.demo.client.dto.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    private Long permissionId;
    private String permissionCode;
    private String permissionName;
    private String resourceType;
    private String resourcePath;
}
