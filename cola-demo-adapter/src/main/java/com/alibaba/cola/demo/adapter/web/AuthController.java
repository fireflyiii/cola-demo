package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.adapter.security.AuthHandler;
import com.alibaba.cola.demo.client.common.DomainException;
import com.alibaba.cola.demo.client.common.ErrorCodeResolver;
import com.alibaba.cola.demo.client.common.OperationLog;
import com.alibaba.cola.demo.client.dto.LoginCmd;
import com.alibaba.cola.demo.client.dto.LoginResponse;
import com.alibaba.cola.demo.client.dto.RefreshTokenCmd;
import com.alibaba.cola.demo.client.dto.data.UserDTO;
import com.alibaba.cola.demo.client.api.IAuthService;
import com.alibaba.cola.demo.domain.common.TokenBlacklist;
import com.alibaba.cola.demo.adapter.security.JwtTokenProvider;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 认证控制器
 * 仅负责接收请求和返回响应，认证逻辑由 AuthHandler 处理
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthHandler authHandler;
    private final IAuthService authService;
    private final TokenBlacklist tokenBlacklist;
    private final JwtTokenProvider jwtTokenProvider;
    private final ErrorCodeResolver errorCodeResolver;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @OperationLog(type = "认证", description = "用户登录")
    public SingleResponse<LoginResponse> login(@RequestBody @Valid LoginCmd loginCmd) {
        LoginResponse response = authHandler.login(loginCmd);
        return SingleResponse.of(response);
    }

    /**
     * 刷新AccessToken（Refresh Token Rotation策略）
     */
    @PostMapping("/refresh")
    public SingleResponse<LoginResponse> refresh(@RequestBody @Valid RefreshTokenCmd cmd) {
        LoginResponse response = authHandler.refreshToken(cmd.getRefreshToken());
        return SingleResponse.of(response);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @OperationLog(type = "认证", description = "用户登出")
    public Response logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Response.buildFailure("401", errorCodeResolver.resolve("AUTH_NOT_AUTHENTICATED"));
        }

        // 将Token加入黑名单
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            long remainingSeconds = jwtTokenProvider.getRemainingTime(token);
            tokenBlacklist.add(token, remainingSeconds);
        }

        SecurityContextHolder.clearContext();
        return Response.buildSuccess();
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public SingleResponse<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new DomainException("401", errorCodeResolver.resolve("AUTH_NOT_AUTHENTICATED"));
        }
        return SingleResponse.of(authService.getUserInfo(authentication.getName()));
    }
}
