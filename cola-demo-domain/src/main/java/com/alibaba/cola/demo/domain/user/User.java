package com.alibaba.cola.demo.domain.user;

import lombok.Data;

import java.util.List;

/**
 * 用户实体
 */
@Data
public class User {
    private Long userId;
    private String username;
    private String password;
    private Integer status;
    private List<Role> roles;

    /**
     * 判断用户是否启用
     */
    public boolean isEnabled() {
        return this.status != null && this.status == 1;
    }
}
