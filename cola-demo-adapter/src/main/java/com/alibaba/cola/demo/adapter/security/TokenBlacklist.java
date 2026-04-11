package com.alibaba.cola.demo.adapter.security;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * JWT Token黑名单
 * 用于登出时使Token失效
 */
@Component
public class TokenBlacklist {

    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public TokenBlacklist() {
        // 每分钟清理过期的黑名单条目
        scheduler.scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * 将Token加入黑名单
     */
    public void add(String token, long expireAtMillis) {
        blacklist.put(token, expireAtMillis);
    }

    /**
     * 检查Token是否在黑名单中
     */
    public boolean contains(String token) {
        Long expireAt = blacklist.get(token);
        if (expireAt == null) {
            return false;
        }
        if (System.currentTimeMillis() > expireAt) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> now > entry.getValue());
    }
}
