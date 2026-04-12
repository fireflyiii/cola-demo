package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.domain.user.gateway.UserGateway;
import com.alibaba.cola.demo.infrastructure.convertor.UserAssembler;
import com.alibaba.cola.demo.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 用户网关实现
 */
@Component
@RequiredArgsConstructor
public class UserGatewayImpl implements UserGateway {

    private final UserMapper userMapper;
    private final UserAssembler userAssembler;

    @Override
    public User findByUsername(String username) {
        return userAssembler.toDomain(userMapper.selectByUsername(username));
    }

    @Override
    public List<String> findRoleCodesByUsername(String username) {
        List<String> roleCodes = userMapper.findRoleCodesByUsername(username);
        return roleCodes != null ? roleCodes : Collections.emptyList();
    }

    @Override
    public List<String> findPermissionsByRoleCodes(List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return Collections.emptyList();
        }
        return userMapper.findPermissionsByRoleCodes(roleCodes);
    }
}
