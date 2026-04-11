package com.alibaba.cola.demo.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录令牌数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginData {
    private String token;
    private Long expiresIn;
    private String tokenType;
}
