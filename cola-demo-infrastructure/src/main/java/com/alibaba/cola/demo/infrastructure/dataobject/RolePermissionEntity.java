package com.alibaba.cola.demo.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色权限关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_permission")
public class RolePermissionEntity extends BaseEntity {

    private Long roleId;
    private Long permissionId;
}
