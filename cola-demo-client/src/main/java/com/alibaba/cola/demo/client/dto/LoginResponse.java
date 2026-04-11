package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.demo.client.dto.data.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private boolean success = true;
    private String token;
    private Long expiresIn;
    private String tokenType;
    private UserDTO user;

    public LoginResponse(String token, Long expiresIn, String tokenType, UserDTO user) {
        this.success = true;
        this.token = token;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.user = user;
    }
}
