package com.alibaba.cola.demo.domain.user;

import lombok.Getter;

import java.util.List;

/**
 * 用户领域实体
 */
@Getter
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

    /**
     * 设置用户ID（由Gateway在持久化后调用）
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 设置用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 设置密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 设置状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}
