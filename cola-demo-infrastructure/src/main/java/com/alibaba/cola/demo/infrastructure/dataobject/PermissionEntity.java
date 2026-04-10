package com.alibaba.cola.demo.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class PermissionEntity extends BaseEntity {

    private String permissionCode;
    private String permissionName;
    private String resourceType;
    private String resourcePath;
}
