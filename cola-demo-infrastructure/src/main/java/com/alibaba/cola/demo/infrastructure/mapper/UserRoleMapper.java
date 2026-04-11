package com.alibaba.cola.demo.infrastructure.mapper;

import com.alibaba.cola.demo.infrastructure.dataobject.UserRoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {
}
