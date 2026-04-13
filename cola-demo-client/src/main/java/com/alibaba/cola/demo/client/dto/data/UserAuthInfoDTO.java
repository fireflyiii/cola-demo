package com.alibaba.cola.demo.client.dto.data;

import com.alibaba.cola.dto.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户认证信息DTO
 * 用于Adapter层获取用户认证所需数据，避免直接依赖Domain层
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthInfoDTO extends DTO {

    private String username;
    private String encodedPassword;
    private boolean enabled;
    private List<String> roleCodes;
    private List<String> permissionCodes;
}
