package com.alibaba.cola.demo.adapter.security;

import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.domain.user.gateway.UserGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情服务实现
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserGateway userGateway;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);

        User user = userGateway.findByUsername(username);
        if (user == null) {
            log.warn("User not found in database: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        log.info("User found: id={}, username={}, status={}", user.getUserId(), user.getUsername(), user.getStatus());

        List<String> roleCodes = userGateway.findRoleCodesByUsername(username);
        List<String> permissions = userGateway.findPermissionsByRoleCodes(roleCodes);

        List<SimpleGrantedAuthority> authorities = roleCodes.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);

        log.debug("User {} loaded with {} roles and {} permissions",
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
