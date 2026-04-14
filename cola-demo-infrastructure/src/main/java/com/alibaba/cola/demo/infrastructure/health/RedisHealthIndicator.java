package com.alibaba.cola.demo.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Redis 健康检查
 * 通过 RedissonClient 检测 Redis 连接是否可用
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisHealthIndicator implements HealthIndicator {

    private final RedissonClient redissonClient;

    @Override
    public Health health() {
        try {
            long startTime = System.currentTimeMillis();
            redissonClient.getKeys().countExists();
            long latency = System.currentTimeMillis() - startTime;
            return Health.up()
                    .withDetail("latency", latency + "ms")
                    .build();
        } catch (Exception ex) {
            log.warn("Redis health check failed: {}", ex.getMessage());
            return Health.down()
                    .withDetail("error", ex.getMessage())
                    .build();
        }
    }
}
