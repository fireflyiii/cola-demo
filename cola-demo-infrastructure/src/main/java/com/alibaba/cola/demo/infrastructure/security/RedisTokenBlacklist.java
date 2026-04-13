package com.alibaba.cola.demo.infrastructure.security;

import com.alibaba.cola.demo.domain.common.TokenBlacklist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * JWT Token黑名单实现
 * 基于Redis实现，支持分布式部署，Token到期自动过期
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTokenBlacklist implements TokenBlacklist {

    private static final String KEY_PREFIX = "token:blacklist:";

    private final RedissonClient redissonClient;

    @Override
    public void add(String token, long remainingSeconds) {
        if (remainingSeconds <= 0) {
            return;
        }
        String key = KEY_PREFIX + token;
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set("1", Duration.ofSeconds(remainingSeconds));
        log.debug("Token added to blacklist, expires in {}s", remainingSeconds);
    }

    @Override
    public boolean contains(String token) {
        String key = KEY_PREFIX + token;
        return redissonClient.getBucket(key).isExists();
    }
}
