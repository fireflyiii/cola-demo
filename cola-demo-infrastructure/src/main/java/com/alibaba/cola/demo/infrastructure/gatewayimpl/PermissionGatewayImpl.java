package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.infrastructure.util.PageHelper;
import com.alibaba.cola.demo.client.dto.PermissionPageQry;
import com.alibaba.cola.demo.domain.user.Permission;
import com.alibaba.cola.demo.domain.user.gateway.PermissionGateway;
import com.alibaba.cola.demo.infrastructure.cache.CacheKeys;
import com.alibaba.cola.demo.infrastructure.cache.RedisCacheService;
import com.alibaba.cola.demo.infrastructure.convertor.PermissionAssembler;
import com.alibaba.cola.demo.infrastructure.dataobject.PermissionEntity;
import com.alibaba.cola.demo.infrastructure.dataobject.RolePermissionEntity;
import com.alibaba.cola.demo.infrastructure.mapper.PermissionMapper;
import com.alibaba.cola.demo.infrastructure.mapper.RolePermissionMapper;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
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
    private final RedisCacheService redisCacheService;

    @Override
    public void create(Permission permission) {
        PermissionEntity entity = permissionAssembler.toEntity(permission);
        permissionMapper.insert(entity);
        permission.setPermissionId(entity.getId());
        redisCacheService.evictList(CacheKeys.PERMISSION_LIST);
    }

    @Override
    public List<Permission> listAll() {
        return redisCacheService.getListOrLoad(CacheKeys.PERMISSION_LIST, () -> {
            LambdaQueryWrapper<PermissionEntity> wrapper = new LambdaQueryWrapper<>();
            List<PermissionEntity> entities = permissionMapper.selectList(wrapper);
            return entities.stream()
                    .map(permissionAssembler::toDomain)
                    .collect(Collectors.toList());
        }, Duration.ofSeconds(CacheKeys.PERMISSION_LIST_TTL_SECONDS));
    }

    @Override
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        RolePermissionEntity entity = new RolePermissionEntity();
        entity.setRoleId(roleId);
        entity.setPermissionId(permissionId);
        rolePermissionMapper.insert(entity);
        redisCacheService.evictList(CacheKeys.PERMISSION_LIST);
    }

    @Override
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        LambdaQueryWrapper<RolePermissionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermissionEntity::getRoleId, roleId)
                .eq(RolePermissionEntity::getPermissionId, permissionId);
        rolePermissionMapper.delete(wrapper);
        redisCacheService.evictList(CacheKeys.PERMISSION_LIST);
    }

    @Override
    public PageResponse<Permission> page(PermissionPageQry qry) {
        LambdaQueryWrapper<PermissionEntity> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(qry.getPermissionName())) {
            wrapper.like(PermissionEntity::getPermissionName, qry.getPermissionName());
        }
        if (StringUtils.isNotBlank(qry.getResourceType())) {
            wrapper.eq(PermissionEntity::getResourceType, qry.getResourceType());
        }
        if (StringUtils.isNotBlank(qry.getResourcePath())) {
            wrapper.like(PermissionEntity::getResourcePath, qry.getResourcePath());
        }
        return PageHelper.toPageResponse(
                permissionMapper.selectPage(PageHelper.toPage(qry), wrapper),
                permissionAssembler::toDomain
        );
    }
}
