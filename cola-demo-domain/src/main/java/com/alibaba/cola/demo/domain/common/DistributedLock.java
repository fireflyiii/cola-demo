package com.alibaba.cola.demo.domain.common;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口
 * 领域层定义接口，基础设施层提供实现
 * 微服务场景下共享资源的并发控制
 */
public interface DistributedLock {

    /**
     * 尝试获取锁
     *
     * @param lockKey  锁的Key
     * @param waitTime 等待时间
     * @param leaseTime 持有锁的时间
     * @param unit     时间单位
     * @return 是否获取成功
     */
    boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 释放锁
     *
     * @param lockKey 锁的Key
     */
    void unlock(String lockKey);
}
