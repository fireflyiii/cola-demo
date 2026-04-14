package com.alibaba.cola.demo.gateway.config;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 网关 Sentinel 配置
 * 限流/熔断时统一返回 JSON 格式响应
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class GatewaySentinelConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public ErrorWebExceptionHandler sentinelGatewayExceptionHandler() {
        return new ErrorWebExceptionHandler() {
            @Override
            public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
                if (ex instanceof BlockException) {
                    log.warn("Gateway Sentinel blocked: {}", exchange.getRequest().getURI().getPath());
                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    try {
                        String body = objectMapper.writeValueAsString(Map.of(
                                "success", false,
                                "errCode", "429",
                                "errMessage", "Too many requests, please try again later"
                        ));
                        DataBuffer buffer = exchange.getResponse().bufferFactory()
                                .wrap(body.getBytes(StandardCharsets.UTF_8));
                        return exchange.getResponse().writeWith(Mono.just(buffer));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                }
                return Mono.error(ex);
            }
        };
    }
}
