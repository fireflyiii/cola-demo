package com.alibaba.cola.demo.app.eventhandler;

import com.alibaba.cola.demo.domain.customer.CustomerCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 客户事件处理器
 * 使用TransactionalEventListener确保只在事务提交后处理事件
 */
@Slf4j
@Component
public class CustomerEventHandler {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCustomerCreated(CustomerCreatedEvent event) {
        log.info("Received CustomerCreatedEvent: eventId={}, customerId={}, customerName={}",
                event.getEventId(), event.getCustomerId(), event.getCustomerName());
        // 此处可扩展：发送通知、同步搜索引擎、更新缓存等
    }
}
