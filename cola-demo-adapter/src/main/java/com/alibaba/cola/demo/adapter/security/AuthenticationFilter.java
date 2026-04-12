package com.alibaba.cola.demo.adapter.security;

import com.alibaba.cola.demo.client.api.IApiAppService;
import com.alibaba.cola.demo.client.dto.data.ApiAppDTO;
import com.alibaba.cola.dto.SingleResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 统一认证过滤器
 * 支持两种认证方式，按优先级依次尝试：
 * 1. API Key（X-API-Key 请求头）— 用于第三方系统调用
 * 2. JWT Bearer Token（Authorization 请求头）— 用于内部用户登录
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final IApiAppService apiAppService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklist tokenBlacklist;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 优先尝试 API Key 认证
        if (tryApiKeyAuthentication(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 其次尝试 JWT 认证
        tryJwtAuthentication(request);

        filterChain.doFilter(request, response);
    }

    /**
     * API Key 认证
     */
    private boolean tryApiKeyAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-Key");
        if (!StringUtils.hasText(apiKey)) {
            return false;
        }

        try {
            SingleResponse<ApiAppDTO> apiAppResponse = apiAppService.getByApiKey(apiKey);
            if (!apiAppResponse.isSuccess() || apiAppResponse.getData() == null) {
                log.warn("Invalid API Key: {}***", apiKey.substring(0, Math.min(apiKey.length(), 10)));
                request.setAttribute("authError", "API Key无效");
                return false;
            }

            ApiAppDTO apiApp = apiAppResponse.getData();

            // 校验状态
            if (apiApp.getStatus() == null || apiApp.getStatus() != 1) {
                log.warn("API Key应用已禁用: appName={}", apiApp.getAppName());
                request.setAttribute("authError", "API应用已禁用");
                return false;
            }

            // 校验过期
            if (apiApp.getExpiresAt() != null && apiApp.getExpiresAt().isBefore(LocalDateTime.now())) {
                log.warn("API Key应用已过期: appName={}", apiApp.getAppName());
                request.setAttribute("authError", "API应用已过期");
                return false;
            }

            // 校验路径权限
            String requestPath = request.getRequestURI();
            if (!isPathAllowed(apiApp.getAllowedPaths(), requestPath)) {
                log.warn("API Key无权访问: appName={}, path={}", apiApp.getAppName(), requestPath);
                request.setAttribute("authError", "API应用无权访问该路径");
                return false;
            }

            // 设置认证
            setAuthentication(apiApp.getAppName(), List.of(new SimpleGrantedAuthority("ROLE_API")), request);
            log.debug("API Key认证成功: appName={}, path={}", apiApp.getAppName(), requestPath);
            return true;
        } catch (Exception ex) {
            log.error("API Key认证异常", ex);
            request.setAttribute("authError", "API Key认证失败");
            return false;
        }
    }

    /**
     * JWT 认证
     */
    private void tryJwtAuthentication(HttpServletRequest request) {
        String jwt = extractJwt(request);
        if (!StringUtils.hasText(jwt)) {
            return;
        }

        try {
            if (jwtTokenProvider.validateToken(jwt)) {
                if (tokenBlacklist.contains(jwt)) {
                    log.debug("Token已注销");
                    request.setAttribute("authError", "Token已被注销");
                    return;
                }
                String username = jwtTokenProvider.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                setAuthentication(userDetails.getUsername(), userDetails.getAuthorities(), request);
                log.debug("JWT认证成功: user={}", username);
            } else {
                log.debug("Token校验失败");
                request.setAttribute("authError", "Token无效或已过期");
            }
        } catch (ExpiredJwtException ex) {
            log.debug("Token已过期: {}", ex.getMessage());
            request.setAttribute("authError", "Token已过期，请重新登录");
        } catch (Exception ex) {
            log.error("JWT认证异常", ex);
            request.setAttribute("authError", "认证失败");
        }
    }

    private void setAuthentication(String principal, Object authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null,
                        authorities instanceof java.util.Collection ? (java.util.Collection<? extends org.springframework.security.core.GrantedAuthority>) authorities
                                : List.of());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String extractJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isPathAllowed(String allowedPaths, String requestPath) {
        if (allowedPaths == null || allowedPaths.trim().isEmpty()) {
            return false;
        }
        for (String pattern : allowedPaths.split(",")) {
            if (matchPath(pattern.trim(), requestPath)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchPath(String pattern, String path) {
        if (!pattern.contains("*")) {
            return pattern.equals(path);
        }
        if (pattern.endsWith("/**")) {
            return path.startsWith(pattern.substring(0, pattern.length() - 3));
        }
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return path.startsWith(prefix) && !path.substring(prefix.length()).contains("/");
        }
        return false;
    }
}
