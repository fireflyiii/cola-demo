package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.adapter.security.JwtTokenProvider;
import com.alibaba.cola.demo.adapter.security.LoginRateLimiter;
import com.alibaba.cola.demo.adapter.security.TokenBlacklist;
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
    private final IAuthService authService;
    private final TokenBlacklist tokenBlacklist;
    private final LoginRateLimiter loginRateLimiter;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginCmd loginCmd) {
        String username = loginCmd.getUsername();
        log.info("Login attempt for user: {}", username);

        // 登录限流检查
        if (!loginRateLimiter.allowLogin(username)) {
            long remainingLock = loginRateLimiter.getRemainingLockTime(username);
            log.warn("Login rate limited for user: {}, locked for {}s", username, remainingLock);
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            return response;
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
        return response;
    }

    /**
     * 刷新AccessToken
     */
    @PostMapping("/refresh")
    public SingleResponse<LoginResponse> refresh(@RequestBody com.alibaba.cola.demo.client.dto.RefreshTokenCmd cmd) {
        Claims claims = jwtTokenProvider.parseToken(cmd.getRefreshToken());
        if (claims == null || !jwtTokenProvider.isRefreshToken(claims)) {
            return SingleResponse.buildFailure("401", "Refresh Token无效或已过期");
        }

        String username = claims.getSubject();
        log.info("Token refresh for user: {}", username);

        LoginResponse newLoginResponse = authService.refreshToken(username, cmd.getRefreshToken());
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
