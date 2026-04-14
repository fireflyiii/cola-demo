package com.alibaba.cola.demo.infrastructure.security;

import com.alibaba.cola.demo.domain.common.TokenBlacklist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

/**
 * JWT Token黑名单实现
 * 基于Redis实现，支持分布式部署，Token到期自动过期
 * Key使用SHA-256哈希，避免完整JWT占用过多内存
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
        String key = KEY_PREFIX + sha256(token);
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set("1", Duration.ofSeconds(remainingSeconds));
        log.debug("Token added to blacklist, expires in {}s", remainingSeconds);
    }

    @Override
    public boolean contains(String token) {
        String key = KEY_PREFIX + sha256(token);
        return redissonClient.getBucket(key).isExists();
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            // SHA-256 是 JDK 标准算法，不会出现此异常
            throw new RuntimeException("SHA-256 algorithm not available", ex);
        }
    }
}
