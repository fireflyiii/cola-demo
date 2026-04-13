package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Command;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录命令
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginCmd extends Command {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
