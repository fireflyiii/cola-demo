package com.alibaba.cola.demo.domain.user;

import com.alibaba.cola.demo.client.common.BizErrorCode;
import com.alibaba.cola.demo.client.common.DomainException;
import com.alibaba.cola.demo.domain.common.AggregateRoot;
import lombok.Getter;

import java.util.List;

/**
 * 用户聚合根
 */
@Getter
public class User implements AggregateRoot {

    private Long userId;
    private String username;
    private Password password;
    private Integer status;
    private List<Role> roles;

    /**
     * 判断用户是否启用
     */
    public boolean isEnabled() {
        return this.status != null && this.status == 1;
    }

    /**
     * 验证用户是否可用
     */
    public void validateAccessible() {
        if (!isEnabled()) {
            throw new DomainException(BizErrorCode.B_USER_DISABLED);
        }
    }

    /**
     * 设置用户ID（由Gateway在持久化后调用）
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 重建用户实体（由Gateway从持久化层加载时使用）
     */
    public static User rebuild(Long userId, String username, String encodedPassword, Integer status) {
        User user = new User();
        user.userId = userId;
        user.username = username;
        user.password = Password.ofEncoded(encodedPassword);
        user.status = status;
        return user;
    }
}
