package com.alibaba.cola.demo.infrastructure.feign;

import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Feign 客户端全局配置
 * 设置超时时间和重试策略
 * 实际超时和重试策略建议通过 Apollo 配置中心动态调整
 */
@Configuration
public class FeignClientConfig {

    /**
     * Feign 请求超时配置
     * connectTimeout: 建立连接的超时时间
     * readTimeout: 读取响应的超时时间
     */
    @Bean
    public Request.Options options() {
        return new Request.Options(
                5, TimeUnit.SECONDS,   // 连接超时 5s
                10, TimeUnit.SECONDS,  // 读取超时 10s
                true                   // 跟随重定向
        );
    }

    /**
     * Feign 重试配置
     * Sentinel 已提供熔断降级能力，Feign 侧不重试避免请求放大
     * 如需重试，建议在业务层通过消息队列实现异步重试
     */
    @Bean
    public Retryer retryer() {
        return Retryer.NEVER_RETRY;
    }
}
