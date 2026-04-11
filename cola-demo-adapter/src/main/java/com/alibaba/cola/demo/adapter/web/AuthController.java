package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.adapter.security.JwtTokenProvider;
import com.alibaba.cola.demo.client.dto.LoginCmd;
import com.alibaba.cola.demo.client.dto.LoginData;
import com.alibaba.cola.demo.client.dto.LoginResponse;
import com.alibaba.cola.demo.client.dto.data.UserDTO;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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

        log.info("User {} logged in successfully, token expires in {} seconds",
                loginCmd.getUsername(), expiresIn);

        List<String> roles = authentication.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        return new LoginResponse(
                new LoginData(token, expiresIn, "Bearer"),
                UserDTO.builder()
                        .username(authentication.getName())
                        .roles(roles)
                        .build()
        );
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Response logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Response.buildFailure("401", "Not authenticated");
        }

        String username = authentication.getName();
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

        List<String> roles = authentication.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        return SingleResponse.of(UserDTO.builder()
                .username(authentication.getName())
                .roles(roles)
                .build());
    }
}
