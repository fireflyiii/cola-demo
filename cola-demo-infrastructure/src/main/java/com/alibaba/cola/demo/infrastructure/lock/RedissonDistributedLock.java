package com.alibaba.cola.demo.infrastructure.lock;

import com.alibaba.cola.demo.domain.common.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 基于 Redisson 的分布式锁实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonDistributedLock implements DistributedLock {

    private static final String KEY_PREFIX = "lock:";

    private final RedissonClient redissonClient;

    @Override
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        String key = KEY_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(key);
        try {
            boolean acquired = lock.tryLock(waitTime, leaseTime, unit);
            if (acquired) {
                log.debug("Lock acquired: {}", key);
            } else {
                log.warn("Lock acquisition failed: {}", key);
            }
            return acquired;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("Lock acquisition interrupted: {}", key);
            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {
        String key = KEY_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            log.debug("Lock released: {}", key);
        }
    }
}
