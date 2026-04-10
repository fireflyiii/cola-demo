package com.alibaba.cola.demo.domain.user;

import lombok.Data;

/**
 * 权限实体
 */
@Data
public class Permission {
    private Long permissionId;
    private String permissionCode;
    private String permissionName;
    private String resourceType;
    private String resourcePath;
}
