package com.alibaba.cola.demo.adapter.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * JWT Token黑名单
 * 基于Redis实现，支持分布式部署，Token到期自动过期
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenBlacklist {

    private static final String KEY_PREFIX = "token:blacklist:";

    private final RedissonClient redissonClient;

    /**
     * 将Token加入黑名单
     *
     * @param token           JWT Token
     * @param remainingSeconds 剩余有效时间（秒）
     */
    public void add(String token, long remainingSeconds) {
        if (remainingSeconds <= 0) {
            return;
        }
        String key = KEY_PREFIX + token;
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set("1", Duration.ofSeconds(remainingSeconds));
        log.debug("Token added to blacklist, expires in {}s", remainingSeconds);
    }

    /**
     * 检查Token是否在黑名单中
     */
    public boolean contains(String token) {
        String key = KEY_PREFIX + token;
        return redissonClient.getBucket(key).isExists();
    }
}
