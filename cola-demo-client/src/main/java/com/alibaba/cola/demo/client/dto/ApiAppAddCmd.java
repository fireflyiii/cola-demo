package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApiAppAddCmd extends Command {

    @NotBlank(message = "应用名称不能为空")
    private String appName;

    /**
     * 允许访问的路径，逗号分隔的Ant模式，如：/order/**,/customer/list
     */
    @NotBlank(message = "允许路径不能为空")
    private String allowedPaths;
}
