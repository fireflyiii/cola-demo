package com.alibaba.cola.demo.adapter.security;

import com.alibaba.cola.demo.domain.common.CurrentUserProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 基于Spring Security的当前用户提供者
 * 从SecurityContext中获取当前登录用户信息
 */
@Slf4j
@Component
public class SecurityCurrentUserProvider implements CurrentUserProvider {

    @Override
    public String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.debug("Failed to get current username from SecurityContext", e);
        }
        return "system";
    }
}
