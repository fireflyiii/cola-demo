package com.alibaba.cola.demo.adapter.config;

import com.alibaba.cola.demo.client.common.ErrorCodeResolver;
import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.RequestOriginParser;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sentinel 配置
 * 定义限流异常处理和来源解析，支持 i18n
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SentinelConfig {

    private final ObjectMapper objectMapper;
    private final ErrorCodeResolver errorCodeResolver;

    /**
     * 自定义 BlockException 处理
     * 统一返回 COLA Response 格式
     */
    @Bean
    public BlockExceptionHandler blockExceptionHandler() {
        return (HttpServletRequest request, HttpServletResponse response, String resourceName, BlockException ex) -> {
            log.warn("Sentinel blocked: {} - {}", request.getRequestURI(), ex.getRule());

            String errCode;
            String errMessage;
            if (ex instanceof FlowException) {
                errCode = "429";
                errMessage = errorCodeResolver.resolve("SENTINEL_FLOW");
            } else if (ex instanceof DegradeException) {
                errCode = "429";
                errMessage = errorCodeResolver.resolve("SENTINEL_DEGRADE");
            } else if (ex instanceof ParamFlowException) {
                errCode = "429";
                errMessage = errorCodeResolver.resolve("SENTINEL_PARAM_FLOW");
            } else if (ex instanceof SystemBlockException) {
                errCode = "503";
                errMessage = errorCodeResolver.resolve("SENTINEL_SYSTEM");
            } else if (ex instanceof AuthorityException) {
                errCode = "403";
                errMessage = errorCodeResolver.resolve("SENTINEL_AUTHORITY");
            } else {
                errCode = "429";
                errMessage = errorCodeResolver.resolve("SENTINEL_DEFAULT");
            }

            response.setStatus(Integer.parseInt(errCode));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(
                    com.alibaba.cola.dto.Response.buildFailure(errCode, errMessage)));
        };
    }

    /**
     * 请求来源解析（可用于基于来源的授权规则）
     */
    @Bean
    public RequestOriginParser requestOriginParser() {
        return request -> {
            String origin = request.getHeader("X-Origin");
            return origin != null ? origin : "default";
        };
    }
}
