package com.alibaba.cola.demo.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * RocketMQ 健康检查
 * 检测 RocketMQTemplate Producer 是否可用
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "rocketmq.enabled", havingValue = "true", matchIfMissing = true)
public class RocketMQHealthIndicator implements HealthIndicator {

    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public Health health() {
        try {
            var producer = rocketMQTemplate.getProducer();
            if (producer != null) {
                return Health.up()
                        .withDetail("producerGroup", producer.getProducerGroup())
                        .build();
            }
            return Health.down()
                    .withDetail("error", "RocketMQ producer is not available")
                    .build();
        } catch (Exception ex) {
            log.warn("RocketMQ health check failed: {}", ex.getMessage());
            return Health.down()
                    .withDetail("error", ex.getMessage())
                    .build();
        }
    }
}
