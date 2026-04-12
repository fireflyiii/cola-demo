package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.domain.user.Permission;
import com.alibaba.cola.demo.domain.user.gateway.PermissionGateway;
import com.alibaba.cola.demo.infrastructure.convertor.PermissionAssembler;
import com.alibaba.cola.demo.infrastructure.dataobject.PermissionEntity;
import com.alibaba.cola.demo.infrastructure.dataobject.RolePermissionEntity;
import com.alibaba.cola.demo.infrastructure.mapper.PermissionMapper;
import com.alibaba.cola.demo.infrastructure.mapper.RolePermissionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限网关实现
 */
@Component
@RequiredArgsConstructor
public class PermissionGatewayImpl implements PermissionGateway {

    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionAssembler permissionAssembler;

    @Override
    public void create(Permission permission) {
        PermissionEntity entity = permissionAssembler.toEntity(permission);
        permissionMapper.insert(entity);
        permission.setPermissionId(entity.getId());
    }

    @Override
    public List<Permission> listAll() {
        LambdaQueryWrapper<PermissionEntity> wrapper = new LambdaQueryWrapper<>();
        List<PermissionEntity> entities = permissionMapper.selectList(wrapper);
        return entities.stream()
                .map(permissionAssembler::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        RolePermissionEntity entity = new RolePermissionEntity();
        entity.setRoleId(roleId);
        entity.setPermissionId(permissionId);
        rolePermissionMapper.insert(entity);
    }

    @Override
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        LambdaQueryWrapper<RolePermissionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermissionEntity::getRoleId, roleId)
                .eq(RolePermissionEntity::getPermissionId, permissionId);
        rolePermissionMapper.delete(wrapper);
    }
}
