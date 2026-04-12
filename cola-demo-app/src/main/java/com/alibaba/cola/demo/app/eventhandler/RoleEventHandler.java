package com.alibaba.cola.demo.app.eventhandler;

import com.alibaba.cola.demo.domain.user.RoleCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 角色事件处理器
 * 演示领域事件的消费方式
 */
@Slf4j
@Component
public class RoleEventHandler {

    @EventListener
    public void handleRoleCreated(RoleCreatedEvent event) {
        log.info("Received RoleCreatedEvent: roleId={}, roleCode={}",
                event.getRoleId(), event.getRoleCode());
        // 此处可扩展：同步权限缓存、发送通知等
    }
}
