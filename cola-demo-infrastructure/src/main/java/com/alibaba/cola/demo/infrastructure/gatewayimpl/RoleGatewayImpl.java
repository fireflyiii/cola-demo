package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.infrastructure.util.PageHelper;
import com.alibaba.cola.demo.client.dto.RolePageQry;
import com.alibaba.cola.demo.domain.user.Role;
import com.alibaba.cola.demo.domain.user.gateway.RoleGateway;
import com.alibaba.cola.demo.infrastructure.cache.CacheKeys;
import com.alibaba.cola.demo.infrastructure.cache.RedisCacheService;
import com.alibaba.cola.demo.infrastructure.convertor.RoleAssembler;
import com.alibaba.cola.demo.infrastructure.dataobject.RoleEntity;
import com.alibaba.cola.demo.infrastructure.dataobject.UserRoleEntity;
import com.alibaba.cola.demo.infrastructure.mapper.RoleMapper;
import com.alibaba.cola.demo.infrastructure.mapper.UserRoleMapper;
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
 * 角色网关实现
 */
@Component
@RequiredArgsConstructor
public class RoleGatewayImpl implements RoleGateway {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleAssembler roleAssembler;
    private final RedisCacheService redisCacheService;

    @Override
    public void create(Role role) {
        RoleEntity entity = roleAssembler.toEntity(role);
        roleMapper.insert(entity);
        role.setRoleId(entity.getId());
        redisCacheService.evictList(CacheKeys.ROLE_LIST);
    }

    @Override
    public List<Role> listAll() {
        return redisCacheService.getListOrLoad(CacheKeys.ROLE_LIST, () -> {
            LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
            List<RoleEntity> entities = roleMapper.selectList(wrapper);
            return entities.stream()
                    .map(roleAssembler::toDomain)
                    .collect(Collectors.toList());
        }, Duration.ofSeconds(CacheKeys.ROLE_LIST_TTL_SECONDS));
    }

    @Override
    public void assignRoleToUser(Long userId, Long roleId) {
        UserRoleEntity entity = new UserRoleEntity();
        entity.setUserId(userId);
        entity.setRoleId(roleId);
        userRoleMapper.insert(entity);
        redisCacheService.evict(CacheKeys.USER_ROLE_CODES_PREFIX + userId);
    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        LambdaQueryWrapper<UserRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleEntity::getUserId, userId)
                .eq(UserRoleEntity::getRoleId, roleId);
        userRoleMapper.delete(wrapper);
        redisCacheService.evict(CacheKeys.USER_ROLE_CODES_PREFIX + userId);
    }

    @Override
    public PageResponse<Role> page(RolePageQry qry) {
        LambdaQueryWrapper<RoleEntity> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(qry.getRoleName())) {
            wrapper.like(RoleEntity::getRoleName, qry.getRoleName());
        }
        if (qry.getStatus() != null) {
            wrapper.eq(RoleEntity::getStatus, qry.getStatus());
        }
        return PageHelper.toPageResponse(
                roleMapper.selectPage(PageHelper.toPage(qry), wrapper),
                roleAssembler::toDomain
        );
    }
}
