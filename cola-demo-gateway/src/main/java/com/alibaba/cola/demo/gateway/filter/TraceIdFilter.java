package com.alibaba.cola.demo.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 全局 TraceId 过滤器
 * 为每个请求生成唯一 traceId，写入请求头和响应头，支持全链路追踪
 */
@Slf4j
@Component
public class TraceIdFilter implements GlobalFilter, Ordered {

    private static final String HEADER_TRACE_ID = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        exchange.getResponse().getHeaders().add(HEADER_TRACE_ID, traceId);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder.header(HEADER_TRACE_ID, traceId))
                .build();

        log.debug("Gateway traceId: {}", traceId);
        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
