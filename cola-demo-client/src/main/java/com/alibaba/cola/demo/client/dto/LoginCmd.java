package com.alibaba.cola.demo.client.dto;

import lombok.Data;

/**
 * 登录命令
 */
@Data
public class LoginCmd {
    private String username;
    private String password;
}
