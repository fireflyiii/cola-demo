package com.alibaba.cola.demo.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Redis缓存服务
 * 提供通用的缓存读写能力，基于Redisson实现
 * 支持防缓存穿透（空值缓存短TTL）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheService {

    private static final String EMPTY_VALUE_MARKER = "##EMPTY##";
    private static final Duration EMPTY_VALUE_TTL = Duration.ofSeconds(60);
    private static final String EMPTY_SUFFIX = ":empty";
    private static final String LIST_SUFFIX = ":list";

    private final RedissonClient redissonClient;

    /**
     * 获取缓存，不存在时通过loader加载并缓存
     * 支持空值缓存，防止缓存穿透
     *
     * @param key      缓存键
     * @param clazz    值类型
     * @param loader   数据加载器
     * @param duration 缓存过期时间
     * @param <T>      值泛型
     * @return 缓存值，可能为null
     */
    public <T> T getOrLoad(String key, Class<T> clazz, Supplier<T> loader, Duration duration) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        Object cached = bucket.get();
        if (cached != null) {
            if (EMPTY_VALUE_MARKER.equals(cached)) {
                log.debug("Cache hit (empty marker): {}", key);
                return null;
            }
            log.debug("Cache hit: {}", key);
            @SuppressWarnings("unchecked")
            T value = (T) cached;
            return value;
        }
        log.debug("Cache miss: {}", key);
        T value = loader.get();
        if (value != null) {
            bucket.set(value, duration);
        } else {
            // 缓存空值标记，防止缓存穿透
            bucket.set(EMPTY_VALUE_MARKER, EMPTY_VALUE_TTL);
            log.debug("Cache empty marker set: {}", key);
        }
        return value;
    }

    /**
     * 获取列表缓存，不存在时通过loader加载并缓存
     * 空列表也会缓存，防止缓存穿透
     * 使用 :list 后缀避免与单值缓存的 Key 冲突
     */
    public <T> List<T> getListOrLoad(String key, Supplier<List<T>> loader, Duration duration) {
        String listKey = key + LIST_SUFFIX;
        String emptyKey = key + EMPTY_SUFFIX;

        // 检查空值标记
        RBucket<String> emptyMarker = redissonClient.getBucket(emptyKey);
        if (emptyMarker.isExists()) {
            log.debug("Cache hit (empty list marker): {}", key);
            return Collections.emptyList();
        }

        RList<T> list = redissonClient.getList(listKey);
        if (!list.isEmpty()) {
            log.debug("Cache hit (list): {}", key);
            return list.readAll();
        }

        log.debug("Cache miss (list): {}", key);
        List<T> value = loader.get();
        if (value == null) {
            value = Collections.emptyList();
        }
        if (!value.isEmpty()) {
            list.clear();
            list.addAll(value);
            list.expire(duration);
        } else {
            // 空列表设置短TTL，防止缓存穿透
            emptyMarker.set(EMPTY_VALUE_MARKER, EMPTY_VALUE_TTL);
            log.debug("Cache empty list marker set: {}", key);
        }
        return value;
    }

    /**
     * 删除缓存
     */
    public void evict(String key) {
        redissonClient.getBucket(key).delete();
        log.debug("Cache evicted: {}", key);
    }

    /**
     * 删除列表缓存（同时清理列表和空值标记）
     */
    public void evictList(String key) {
        redissonClient.getList(key + LIST_SUFFIX).delete();
        redissonClient.getBucket(key + EMPTY_SUFFIX).delete();
        log.debug("Cache evicted (list): {}", key);
    }
}
