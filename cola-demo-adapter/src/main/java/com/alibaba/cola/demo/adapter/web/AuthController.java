package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.adapter.security.JwtTokenProvider;
import com.alibaba.cola.demo.domain.common.LoginRateLimiter;
import com.alibaba.cola.demo.domain.common.TokenBlacklist;
import com.alibaba.cola.demo.client.common.BizErrorCode;
import com.alibaba.cola.demo.client.common.DomainException;
import com.alibaba.cola.demo.client.dto.LoginCmd;
import com.alibaba.cola.demo.client.dto.LoginResponse;
import com.alibaba.cola.demo.client.dto.data.UserDTO;
import com.alibaba.cola.demo.client.api.IAuthService;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final IAuthService authService;
    private final TokenBlacklist tokenBlacklist;
    private final LoginRateLimiter loginRateLimiter;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public SingleResponse<LoginResponse> login(@RequestBody @Valid LoginCmd loginCmd) {
        String username = loginCmd.getUsername();
        log.info("Login attempt for user: {}", username);

        // 登录限流检查
        if (!loginRateLimiter.allowLogin(username)) {
            long remainingLock = loginRateLimiter.getRemainingLockTime(username);
            log.warn("Login rate limited for user: {}, locked for {}s", username, remainingLock);
            throw new DomainException(BizErrorCode.B_AUTH_LOGIN_RATE_LIMITED);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginCmd.getUsername(),
                        loginCmd.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        Long expiresIn = jwtTokenProvider.getExpirationTime();

        // 登录成功，清除限流计数
        loginRateLimiter.clearAttempts(username);

        LoginResponse response = authService.login(loginCmd, accessToken, refreshToken, expiresIn);
        log.info("User {} logged in successfully", username);
        return SingleResponse.of(response);
    }

    /**
     * 刷新AccessToken
     * 使用Refresh Token Rotation策略：每次刷新后旧Refresh Token失效
     */
    @PostMapping("/refresh")
    public SingleResponse<LoginResponse> refresh(@RequestBody com.alibaba.cola.demo.client.dto.RefreshTokenCmd cmd) {
        String oldRefreshToken = cmd.getRefreshToken();

        // 检查旧Refresh Token是否已在黑名单中（防止重放）
        if (tokenBlacklist.contains(oldRefreshToken)) {
            return SingleResponse.buildFailure("401", "Refresh Token已失效");
        }

        Claims claims = jwtTokenProvider.parseToken(oldRefreshToken);
        if (claims == null || !jwtTokenProvider.isRefreshToken(claims)) {
            return SingleResponse.buildFailure("401", "Refresh Token无效或已过期");
        }

        String username = claims.getSubject();
        log.info("Token refresh for user: {}", username);

        // 将旧Refresh Token加入黑名单（Refresh Token Rotation）
        long remainingSeconds = jwtTokenProvider.getRemainingTime(oldRefreshToken);
        tokenBlacklist.add(oldRefreshToken, remainingSeconds);

        // 加载用户信息并生成新Token
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String newAccessToken = jwtTokenProvider.generateToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        Long expiresIn = jwtTokenProvider.getExpirationTime();

        LoginResponse newLoginResponse = authService.refreshToken(username, newAccessToken, newRefreshToken, expiresIn);
        log.info("Token refreshed successfully for user: {}", username);
        return SingleResponse.of(newLoginResponse);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Response logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Response.buildFailure("401", "Not authenticated");
        }

        String username = authentication.getName();

        // 将Token加入黑名单
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            long remainingSeconds = jwtTokenProvider.getRemainingTime(token);
            tokenBlacklist.add(token, remainingSeconds);
        }

        SecurityContextHolder.clearContext();

        log.info("User {} logged out", username);
        return Response.buildSuccess();
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public SingleResponse<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return SingleResponse.buildFailure("401", "Not authenticated");
        }
        return SingleResponse.of(authService.getUserInfo(authentication.getName()));
    }
}
