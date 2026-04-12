package com.alibaba.cola.demo.client.dto.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户认证信息DTO
 * 用于Adapter层获取用户认证所需数据，避免直接依赖Domain层
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthInfo {

    private String username;
    private String encodedPassword;
    private boolean enabled;
    private List<String> roleCodes;
    private List<String> permissionCodes;
}
