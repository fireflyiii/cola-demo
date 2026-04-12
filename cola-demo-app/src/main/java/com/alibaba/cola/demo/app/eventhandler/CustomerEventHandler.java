package com.alibaba.cola.demo.app.eventhandler;

import com.alibaba.cola.demo.domain.customer.CustomerCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 客户事件处理器
 * 演示领域事件的消费方式
 */
@Slf4j
@Component
public class CustomerEventHandler {

    @EventListener
    public void handleCustomerCreated(CustomerCreatedEvent event) {
        log.info("Received CustomerCreatedEvent: customerId={}, customerName={}",
                event.getCustomerId(), event.getCustomerName());
        // 此处可扩展：发送通知、同步搜索引擎、更新缓存等
    }
}
