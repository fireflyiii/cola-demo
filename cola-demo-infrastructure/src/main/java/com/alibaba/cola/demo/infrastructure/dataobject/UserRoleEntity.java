package com.alibaba.cola.demo.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
public class UserRoleEntity extends BaseEntity {

    private Long userId;
    private Long roleId;
}
