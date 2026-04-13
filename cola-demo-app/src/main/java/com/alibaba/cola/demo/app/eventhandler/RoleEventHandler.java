package com.alibaba.cola.demo.app.eventhandler;

import com.alibaba.cola.demo.domain.user.RoleCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 角色事件处理器
 * 使用TransactionalEventListener确保只在事务提交后处理事件
 */
@Slf4j
@Component
public class RoleEventHandler {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRoleCreated(RoleCreatedEvent event) {
        log.info("Received RoleCreatedEvent: eventId={}, roleId={}, roleCode={}",
                event.getEventId(), event.getRoleId(), event.getRoleCode());
        // 此处可扩展：同步权限缓存、发送通知等
    }
}
