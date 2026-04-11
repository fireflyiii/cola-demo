package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.domain.user.gateway.UserGateway;
import com.alibaba.cola.demo.infrastructure.convertor.UserConvertor;
import com.alibaba.cola.demo.infrastructure.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 用户网关实现
 */
@Component
public class UserGatewayImpl implements UserGateway {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        return UserConvertor.toDomain(userMapper.selectByUsername(username));
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

    @Override
    public boolean updatePassword(String username, String encodedPassword) {
        return userMapper.updatePassword(username, encodedPassword) > 0;
    }
}
