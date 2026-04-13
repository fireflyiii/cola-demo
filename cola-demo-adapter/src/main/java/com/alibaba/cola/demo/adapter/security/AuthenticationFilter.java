package com.alibaba.cola.demo.adapter.security;

import com.alibaba.cola.demo.client.api.IApiAppService;
import com.alibaba.cola.demo.client.dto.data.ApiAppDTO;
import com.alibaba.cola.demo.domain.common.PathMatcher;
import com.alibaba.cola.demo.domain.common.TokenBlacklist;
import com.alibaba.cola.dto.SingleResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
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
import java.util.Collection;
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

    private static final String HEADER_API_KEY = "X-API-Key";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final IApiAppService apiAppService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklist tokenBlacklist;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(HEADER_API_KEY);

        // 如果提供了 API Key，仅尝试 API Key 认证
        if (StringUtils.hasText(apiKey)) {
            tryApiKeyAuthentication(request, apiKey);
            filterChain.doFilter(request, response);
            return;
        }

        // 否则尝试 JWT 认证
        tryJwtAuthentication(request);

        filterChain.doFilter(request, response);
    }

    /**
     * API Key 认证
     */
    private void tryApiKeyAuthentication(HttpServletRequest request, String apiKey) {
        try {
            SingleResponse<ApiAppDTO> apiAppResponse = apiAppService.getByApiKey(apiKey);
            if (!apiAppResponse.isSuccess() || apiAppResponse.getData() == null) {
                log.warn("Invalid API Key: {}****", maskKey(apiKey));
                request.setAttribute("authError", "API Key无效");
                return;
            }

            ApiAppDTO apiApp = apiAppResponse.getData();

            // 校验状态
            if (apiApp.getStatus() == null || apiApp.getStatus() != 1) {
                log.warn("API Key应用已禁用: appName={}", apiApp.getAppName());
                request.setAttribute("authError", "API应用已禁用");
                return;
            }

            // 校验过期
            if (apiApp.getExpiresAt() != null && apiApp.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
                log.warn("API Key应用已过期: appName={}", apiApp.getAppName());
                request.setAttribute("authError", "API应用已过期");
                return;
            }

            // 校验路径权限 — 委托给领域PathMatcher工具
            String requestPath = request.getRequestURI();
            if (!PathMatcher.isPathAllowed(apiApp.getAllowedPaths(), requestPath)) {
                log.warn("API Key无权访问: appName={}, path={}", apiApp.getAppName(), requestPath);
                request.setAttribute("authError", "API应用无权访问该路径");
                return;
            }

            // 设置认证
            setAuthentication(apiApp.getAppName(),
                    List.of(new SimpleGrantedAuthority("ROLE_API")), request);
            log.debug("API Key认证成功: appName={}, path={}", apiApp.getAppName(), requestPath);
        } catch (Exception ex) {
            log.error("API Key认证异常", ex);
            request.setAttribute("authError", "API Key认证失败");
        }
    }

    /**
     * JWT 认证（单次解析优化）
     */
    private void tryJwtAuthentication(HttpServletRequest request) {
        String jwt = extractJwt(request);
        if (!StringUtils.hasText(jwt)) {
            return;
        }

        // 单次解析JWT，获取Claims
        Claims claims = jwtTokenProvider.parseToken(jwt);
        if (claims == null) {
            log.debug("Token校验失败");
            request.setAttribute("authError", "Token无效或已过期");
            return;
        }

        if (tokenBlacklist.contains(jwt)) {
            log.debug("Token已注销");
            request.setAttribute("authError", "Token已被注销");
            return;
        }

        String username = claims.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        setAuthentication(userDetails.getUsername(), userDetails.getAuthorities(), request);
        log.debug("JWT认证成功: user={}", username);
    }

    private void setAuthentication(String principal, Collection<? extends GrantedAuthority> authorities,
                                   HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String extractJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * 掩码API Key，仅显示前4位
     */
    private String maskKey(String key) {
        if (key == null || key.length() <= 4) {
            return "****";
        }
        return key.substring(0, 4);
    }
}
