package com.alibaba.cola.demo.domain.user;

import com.alibaba.cola.demo.domain.common.DomainEvent;
import lombok.Getter;

/**
 * 角色已创建事件
 */
@Getter
public class RoleCreatedEvent extends DomainEvent {

    private final Long roleId;
    private final String roleCode;

    public RoleCreatedEvent(Long roleId, String roleCode) {
        this.roleId = roleId;
        this.roleCode = roleCode;
    }
}
