package com.alibaba.cola.demo.adapter.config;

import com.alibaba.cola.demo.client.common.ErrorCodeResolver;
import com.alibaba.cola.demo.client.common.ErrorCodeResolverHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * ErrorCodeResolver 初始化器
 * 在应用启动完成后，将 Spring 管理的 ErrorCodeResolver 注入到 Client 层的 Holder 中
 */
@Component
@RequiredArgsConstructor
public class ErrorCodeResolverInitializer {

    private final ErrorCodeResolver errorCodeResolver;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        ErrorCodeResolverHolder.setResolver(errorCodeResolver);
    }
}
