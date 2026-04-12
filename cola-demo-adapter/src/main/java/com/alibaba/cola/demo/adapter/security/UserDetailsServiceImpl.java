package com.alibaba.cola.demo.adapter.security;

import com.alibaba.cola.demo.app.service.AuthService;
import com.alibaba.cola.demo.client.dto.data.UserAuthInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户详情服务实现
 * 通过App层AuthService获取用户认证信息，避免直接依赖Domain层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthService authService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuthInfo authInfo = authService.loadUserAuthInfo(username);
        if (authInfo == null) {
            log.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authInfo.getRoleCodes().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .forEach(authorities::add);
        authInfo.getPermissionCodes().stream()
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);

        log.debug("User {} loaded with {} roles and {} permissions",
                username, authInfo.getRoleCodes().size(), authInfo.getPermissionCodes().size());

        return new org.springframework.security.core.userdetails.User(
                authInfo.getUsername(),
                authInfo.getEncodedPassword(),
                authInfo.isEnabled(),
                true,
                true,
                true,
                authorities
        );
    }
}
