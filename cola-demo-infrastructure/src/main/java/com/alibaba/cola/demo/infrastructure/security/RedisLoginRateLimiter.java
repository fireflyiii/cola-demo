package com.alibaba.cola.demo.infrastructure.security;

import com.alibaba.cola.demo.domain.common.LoginRateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 登录限流器实现
 * 基于Redis实现，防止暴力破解
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLoginRateLimiter implements LoginRateLimiter {

    private static final String KEY_PREFIX = "ratelimit:login:";
    private static final long MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;
    private static final long WINDOW_SECONDS = 300; // 5分钟窗口

    private final RedissonClient redissonClient;

    @Override
    public boolean allowLogin(String username) {
        String key = KEY_PREFIX + username;
        RAtomicLong counter = redissonClient.getAtomicLong(key);

        // 原子操作：首次尝试使用 compareAndSet 保证并发安全
        if (counter.compareAndSet(0, 1)) {
            counter.expire(WINDOW_SECONDS, TimeUnit.SECONDS);
            return true;
        }

        long attempts = counter.get();
        if (attempts >= MAX_ATTEMPTS) {
            log.warn("Login rate limited for user: {}", username);
            // 延长锁定时间
            counter.expire(LOCK_DURATION_MINUTES * 60, TimeUnit.SECONDS);
            return false;
        }

        counter.incrementAndGet();
        return true;
    }

    @Override
    public void clearAttempts(String username) {
        String key = KEY_PREFIX + username;
        redissonClient.getAtomicLong(key).delete();
    }

    @Override
    public long getRemainingLockTime(String username) {
        String key = KEY_PREFIX + username;
        long ttl = redissonClient.getAtomicLong(key).remainTimeToLive();
        return ttl > 0 ? ttl / 1000 : 0;
    }
}
