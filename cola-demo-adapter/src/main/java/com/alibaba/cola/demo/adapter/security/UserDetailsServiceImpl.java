package com.alibaba.cola.demo.adapter.security;

import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.domain.user.gateway.UserGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情服务实现
 *
 * 【架构说明】
 * 虽然UserDetailsService是Spring Security的组件，但考虑到：
 * 1. 它需要依赖Domain的UserGateway接口
 * 2. 认证逻辑是业务的一部分
 * 3. COLA架构中adapter是最外层，聚合各种外部依赖
 *
 * 故将此类放在adapter层，作为认证业务的入口协调器。
 * 如果后续需要更复杂的认证逻辑，可以在app层添加AuthService。
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserGateway userGateway;

    public UserDetailsServiceImpl(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("Loading user by username: {}", username);

        User user = userGateway.findByUsername(username);
        if (user == null) {
            LOGGER.warn("User not found in database: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        LOGGER.info("User found: id={}, username={}, status={}", user.getUserId(), user.getUsername(), user.getStatus());

        List<String> roleCodes = userGateway.findRoleCodesByUsername(username);
        List<String> permissions = userGateway.findPermissionsByRoleCodes(roleCodes);

        List<SimpleGrantedAuthority> authorities = roleCodes.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);

        LOGGER.debug("User {} loaded with {} roles and {} permissions",
                username, roleCodes.size(), permissions.size());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                authorities
        );
    }
}
