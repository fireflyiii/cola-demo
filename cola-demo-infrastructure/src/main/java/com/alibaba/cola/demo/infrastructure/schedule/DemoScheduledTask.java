package com.alibaba.cola.demo.infrastructure.schedule;

import com.alibaba.cola.demo.domain.common.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 分布式定时任务示例
 * 通过 Redisson 分布式锁保证多实例下只执行一次
 *
 * 使用方式：在 @Scheduled 方法中调用 DistributedLock.tryLock()，
 * 获取到锁才执行业务逻辑，未获取到锁说明其他实例已在执行
 *
 * 通过 schedule.enabled=false 可关闭定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "schedule.enabled", havingValue = "true", matchIfMissing = true)
public class DemoScheduledTask {

    private final DistributedLock distributedLock;

    /**
     * 示例：每5分钟执行一次的定时清理任务
     * 分布式锁保证多实例下仅一个实例执行
     */
    @Scheduled(fixedRateString = "${schedule.cleanup.interval:300000}")
    public void cleanupTask() {
        boolean locked = false;
        try {
            locked = distributedLock.tryLock("schedule:cleanup", 0, 60, TimeUnit.SECONDS);
            if (!locked) {
                log.debug("Cleanup task skipped - another instance is running");
                return;
            }
            log.info("Cleanup task started");
            // 执行实际业务逻辑，如清理过期数据、缓存等
            log.info("Cleanup task completed");
        } catch (Exception ex) {
            log.error("Cleanup task failed", ex);
        } finally {
            if (locked) {
                distributedLock.unlock("schedule:cleanup");
            }
        }
    }
}
