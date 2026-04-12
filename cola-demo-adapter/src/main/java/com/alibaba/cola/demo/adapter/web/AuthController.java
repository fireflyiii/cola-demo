package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.adapter.security.JwtTokenProvider;
import com.alibaba.cola.demo.adapter.security.TokenBlacklist;
import com.alibaba.cola.demo.client.dto.LoginCmd;
import com.alibaba.cola.demo.client.dto.LoginResponse;
import com.alibaba.cola.demo.client.dto.data.UserDTO;
import com.alibaba.cola.demo.app.service.AuthService;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final TokenBlacklist tokenBlacklist;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginCmd loginCmd) {
        log.info("Login attempt for user: {} at {}", loginCmd.getUsername(), LocalDateTime.now());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginCmd.getUsername(),
                        loginCmd.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        Long expiresIn = jwtTokenProvider.getExpirationTime();

        LoginResponse response = authService.login(loginCmd, token, expiresIn);
        log.info("User {} logged in successfully", loginCmd.getUsername());
        return response;
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
            long expireAt = System.currentTimeMillis() + jwtTokenProvider.getRemainingTime(token) * 1000;
            tokenBlacklist.add(token, expireAt);
        }

        SecurityContextHolder.clearContext();

        log.info("User {} logged out at {}", username, LocalDateTime.now());
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
