package com.alibaba.cola.demo.domain.user;

import com.alibaba.cola.demo.client.common.BizErrorCode;
import com.alibaba.cola.demo.client.common.DomainException;
import com.alibaba.cola.demo.domain.common.AggregateRoot;
import lombok.Getter;

/**
 * 权限聚合根
 */
@Getter
public class Permission implements AggregateRoot {

    private Long permissionId;
    private String permissionCode;
    private String permissionName;
    private ResourceType resourceType;
    private String resourcePath;

    /**
     * 创建权限
     */
    public static Permission create(String permissionCode, String permissionName, String resourceType, String resourcePath) {
        Permission permission = new Permission();
        permission.permissionCode = permissionCode;
        permission.permissionName = permissionName;
        permission.resourceType = resourceType != null ? ResourceType.valueOf(resourceType) : ResourceType.API;
        permission.resourcePath = resourcePath;
        permission.validate();
        return permission;
    }

    /**
     * 领域行为：校验权限信息
     */
    public void validate() {
        if (permissionCode == null || permissionCode.trim().isEmpty()) {
              throw new DomainException(BizErrorCode.B_PERMISSION_CODE_NOT_BLANK);
          }
          if (permissionName == null || permissionName.trim().isEmpty()) {
              throw new DomainException(BizErrorCode.B_PERMISSION_NAME_NOT_BLANK);
        }
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }
}
