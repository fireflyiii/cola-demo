package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录命令
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginCmd extends Command {
    private String username;
    private String password;
}
