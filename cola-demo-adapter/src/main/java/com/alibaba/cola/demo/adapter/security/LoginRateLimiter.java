package com.alibaba.cola.demo.adapter.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 登录限流器
 * 基于Redis实现，防止暴力破解
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginRateLimiter {

    private static final String KEY_PREFIX = "ratelimit:login:";
    private static final long MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;
    private static final long WINDOW_SECONDS = 300; // 5分钟窗口

    private final RedissonClient redissonClient;

    /**
     * 检查是否允许登录
     *
     * @param username 用户名
     * @return true=允许，false=被限流
     */
    public boolean allowLogin(String username) {
        String key = KEY_PREFIX + username;
        RAtomicLong counter = redissonClient.getAtomicLong(key);

        long attempts = counter.get();
        if (attempts == 0) {
            // 首次尝试，设置窗口过期
            counter.set(1);
            counter.expire(WINDOW_SECONDS, TimeUnit.SECONDS);
            return true;
        }

        if (attempts >= MAX_ATTEMPTS) {
            log.warn("Login rate limited for user: {}", username);
            // 延长锁定时间
            counter.expire(LOCK_DURATION_MINUTES * 60, TimeUnit.SECONDS);
            return false;
        }

        counter.incrementAndGet();
        return true;
    }

    /**
     * 登录成功后清除计数
     */
    public void clearAttempts(String username) {
        String key = KEY_PREFIX + username;
        redissonClient.getAtomicLong(key).delete();
    }

    /**
     * 获取剩余锁定时间（秒）
     */
    public long getRemainingLockTime(String username) {
        String key = KEY_PREFIX + username;
        long ttl = redissonClient.getAtomicLong(key).remainTimeToLive();
        return ttl > 0 ? ttl / 1000 : 0;
    }
}
