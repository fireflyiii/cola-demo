package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.domain.user.Role;
import com.alibaba.cola.demo.domain.user.gateway.RoleGateway;
import com.alibaba.cola.demo.infrastructure.convertor.RoleAssembler;
import com.alibaba.cola.demo.infrastructure.dataobject.RoleEntity;
import com.alibaba.cola.demo.infrastructure.dataobject.UserRoleEntity;
import com.alibaba.cola.demo.infrastructure.mapper.RoleMapper;
import com.alibaba.cola.demo.infrastructure.mapper.UserRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色网关实现
 */
@Component
@RequiredArgsConstructor
public class RoleGatewayImpl implements RoleGateway {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleAssembler roleAssembler;

    @Override
    public void create(Role role) {
        RoleEntity entity = roleAssembler.toEntity(role);
        roleMapper.insert(entity);
        role.setRoleId(entity.getId());
    }

    @Override
    public List<Role> listAll() {
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
        List<RoleEntity> entities = roleMapper.selectList(wrapper);
        return entities.stream()
                .map(roleAssembler::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void assignRoleToUser(Long userId, Long roleId) {
        UserRoleEntity entity = new UserRoleEntity();
        entity.setUserId(userId);
        entity.setRoleId(roleId);
        userRoleMapper.insert(entity);
    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        LambdaQueryWrapper<UserRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleEntity::getUserId, userId)
                .eq(UserRoleEntity::getRoleId, roleId);
        userRoleMapper.delete(wrapper);
    }
}
