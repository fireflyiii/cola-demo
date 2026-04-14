package com.alibaba.cola.demo.adapter.security;

import com.alibaba.cola.demo.client.common.BizErrorCode;
import com.alibaba.cola.demo.client.common.DomainException;
import com.alibaba.cola.demo.client.common.ErrorCodeResolver;
import com.alibaba.cola.demo.client.dto.LoginCmd;
import com.alibaba.cola.demo.client.dto.LoginResponse;
import com.alibaba.cola.demo.client.dto.data.UserDTO;
import com.alibaba.cola.demo.domain.common.LoginRateLimiter;
import com.alibaba.cola.demo.domain.common.TokenBlacklist;
import com.alibaba.cola.demo.client.api.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * 认证处理器（Adapter层）
 * 处理JWT Token生成、Spring Security认证等适配器层关注点
 * 对外通过 IAuthService 接口暴露用户信息查询
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginRateLimiter loginRateLimiter;
    private final TokenBlacklist tokenBlacklist;
    private final UserDetailsService userDetailsService;
    private final IAuthService authService;
    private final ErrorCodeResolver errorCodeResolver;

    /**
     * 处理登录请求
     */
    public LoginResponse login(LoginCmd loginCmd) {
        String username = loginCmd.getUsername();
        log.info("Login attempt for user: {}", username);

        // 登录限流检查
        if (!loginRateLimiter.allowLogin(username)) {
            long remainingLock = loginRateLimiter.getRemainingLockTime(username);
            log.warn("Login rate limited for user: {}, locked for {}s", username, remainingLock);
            throw new DomainException(BizErrorCode.B_AUTH_LOGIN_RATE_LIMITED);
        }

        // Spring Security 认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginCmd.getUsername(), loginCmd.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成 Token
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        Long expiresIn = jwtTokenProvider.getExpirationTime();

        // 登录成功，清除限流计数
        loginRateLimiter.clearAttempts(username);

        // 通过领域服务获取用户信息
        UserDTO userDTO = authService.getUserInfo(username);

        log.info("User {} logged in successfully", username);
        return new LoginResponse(accessToken, expiresIn, "Bearer", refreshToken, userDTO);
    }

    /**
     * 处理Token刷新请求
     */
    public LoginResponse refreshToken(String oldRefreshToken) {
        // 检查旧 Refresh Token 是否已在黑名单中（防止重放）
        if (tokenBlacklist.contains(oldRefreshToken)) {
            throw new DomainException("401", errorCodeResolver.resolve("AUTH_REFRESH_TOKEN_REVOKED"));
        }

        io.jsonwebtoken.Claims claims = jwtTokenProvider.parseToken(oldRefreshToken);
        if (claims == null || !jwtTokenProvider.isRefreshToken(claims)) {
            throw new DomainException("401", errorCodeResolver.resolve("AUTH_REFRESH_TOKEN_INVALID"));
        }

        String username = claims.getSubject();
        log.info("Token refresh for user: {}", username);

        // 将旧 Refresh Token加入黑名单（Refresh Token Rotation）
        long remainingSeconds = jwtTokenProvider.getRemainingTime(oldRefreshToken);
        tokenBlacklist.add(oldRefreshToken, remainingSeconds);

        // 加载用户信息并生成新 Token
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String newAccessToken = jwtTokenProvider.generateToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        Long expiresIn = jwtTokenProvider.getExpirationTime();

        // 通过领域服务获取用户信息
        UserDTO userDTO = authService.getUserInfo(username);

        log.info("Token refreshed successfully for user: {}", username);
        return new LoginResponse(newAccessToken, expiresIn, "Bearer", newRefreshToken, userDTO);
    }

    /**
     * 处理登出请求
     */
    public void logout(HttpServletRequestAccessor requestAccessor) {
        String bearerToken = requestAccessor.getBearerToken();
        if (bearerToken != null) {
            long remainingSeconds = jwtTokenProvider.getRemainingTime(bearerToken);
            tokenBlacklist.add(bearerToken, remainingSeconds);
        }
        SecurityContextHolder.clearContext();
    }

    /**
     * 请求访问器接口（用于解耦 HttpServletRequest）
     */
    public interface HttpServletRequestAccessor {
        String getBearerToken();
    }
}
