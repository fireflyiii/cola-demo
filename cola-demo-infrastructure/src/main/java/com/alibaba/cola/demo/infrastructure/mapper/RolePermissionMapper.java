package com.alibaba.cola.demo.infrastructure.mapper;

import com.alibaba.cola.demo.infrastructure.dataobject.RolePermissionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联Mapper
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermissionEntity> {
}
