package com.alibaba.cola.demo.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

/**
 * Redis缓存服务
 * 提供通用的缓存读写能力，基于Redisson实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedissonClient redissonClient;

    /**
     * 获取缓存，不存在时通过loader加载并缓存
     *
     * @param key      缓存键
     * @param clazz    值类型
     * @param loader   数据加载器
     * @param duration 缓存过期时间
     * @param <T>      值泛型
     * @return 缓存值
     */
    public <T> T getOrLoad(String key, Class<T> clazz, Supplier<T> loader, Duration duration) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        T value = bucket.get();
        if (value != null) {
            log.debug("Cache hit: {}", key);
            return value;
        }
        log.debug("Cache miss: {}", key);
        value = loader.get();
        if (value != null) {
            bucket.set(value, duration);
        }
        return value;
    }

    /**
     * 获取列表缓存，不存在时通过loader加载并缓存
     */
    public <T> List<T> getListOrLoad(String key, Supplier<List<T>> loader, Duration duration) {
        RList<T> list = redissonClient.getList(key);
        if (!list.isEmpty()) {
            log.debug("Cache hit (list): {}", key);
            return list.readAll();
        }
        log.debug("Cache miss (list): {}", key);
        List<T> value = loader.get();
        if (value != null && !value.isEmpty()) {
            list.clear();
            list.addAll(value);
            list.expire(duration);
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
     * 删除列表缓存
     */
    public void evictList(String key) {
        redissonClient.getList(key).delete();
        log.debug("Cache evicted (list): {}", key);
    }
}
