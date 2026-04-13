package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 刷新Token命令
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RefreshTokenCmd extends Command {

    private String refreshToken;
}
