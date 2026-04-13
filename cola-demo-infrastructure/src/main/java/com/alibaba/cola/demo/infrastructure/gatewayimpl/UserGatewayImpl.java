package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.domain.user.gateway.UserGateway;
import com.alibaba.cola.demo.infrastructure.cache.CacheKeys;
import com.alibaba.cola.demo.infrastructure.cache.RedisCacheService;
import com.alibaba.cola.demo.infrastructure.convertor.UserAssembler;
import com.alibaba.cola.demo.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

/**
 * 用户网关实现
 */
@Component
@RequiredArgsConstructor
public class UserGatewayImpl implements UserGateway {

    private final UserMapper userMapper;
    private final UserAssembler userAssembler;
    private final RedisCacheService redisCacheService;

    @Override
    public User findByUsername(String username) {
        return userAssembler.toDomain(userMapper.selectByUsername(username));
    }

    @Override
    public List<String> findRoleCodesByUsername(String username) {
        String cacheKey = CacheKeys.USER_ROLE_CODES_PREFIX + username;
        return redisCacheService.getListOrLoad(cacheKey, () -> {
            List<String> roleCodes = userMapper.findRoleCodesByUsername(username);
            return roleCodes != null ? roleCodes : Collections.emptyList();
        }, Duration.ofSeconds(CacheKeys.USER_AUTH_TTL_SECONDS));
    }

    @Override
    public List<String> findPermissionsByRoleCodes(List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return Collections.emptyList();
        }
        String cacheKey = CacheKeys.USER_PERMISSION_CODES_PREFIX + hashRoleCodes(roleCodes);
        return redisCacheService.getListOrLoad(cacheKey, () ->
                userMapper.findPermissionsByRoleCodes(roleCodes)
        , Duration.ofSeconds(CacheKeys.USER_AUTH_TTL_SECONDS));
    }

    /**
     * 使用排序后的角色编码拼接作为缓存key，确保确定性和一致性
     */
    private String hashRoleCodes(List<String> roleCodes) {
        List<String> sorted = roleCodes.stream().sorted(Comparator.naturalOrder()).toList();
        StringJoiner joiner = new StringJoiner(",");
        sorted.forEach(joiner::add);
        return joiner.toString();
    }
}
