package com.alibaba.cola.demo.infrastructure.lock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RedissonDistributedLock 单元测试
 * 验证加锁/解锁行为和中断处理
 */
@ExtendWith(MockitoExtension.class)
class RedissonDistributedLockTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    private RedissonDistributedLock distributedLock;

    @BeforeEach
    void setUp() {
        distributedLock = new RedissonDistributedLock(redissonClient);
    }

    @Test
    void shouldReturnTrueWhenLockAcquired() throws InterruptedException {
        doReturn(rLock).when(redissonClient).getLock(anyString());
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        boolean result = distributedLock.tryLock("order:123", 5, 30, TimeUnit.SECONDS);

        assertTrue(result);
        verify(redissonClient).getLock(eq("lock:order:123"));
    }

    @Test
    void shouldReturnFalseWhenLockNotAcquired() throws InterruptedException {
        doReturn(rLock).when(redissonClient).getLock(anyString());
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(false);

        boolean result = distributedLock.tryLock("order:123", 1, 10, TimeUnit.SECONDS);

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenInterrupted() throws InterruptedException {
        doReturn(rLock).when(redissonClient).getLock(anyString());
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class)))
                .thenThrow(new InterruptedException("test"));

        boolean result = distributedLock.tryLock("order:123", 1, 10, TimeUnit.SECONDS);

        assertFalse(result);
        assertTrue(Thread.currentThread().isInterrupted());
        // 清除中断标记，避免影响其他测试
        Thread.interrupted();
    }

    @Test
    void shouldUnlockWhenHeldByCurrentThread() {
        doReturn(rLock).when(redissonClient).getLock(anyString());
        when(rLock.isHeldByCurrentThread()).thenReturn(true);

        distributedLock.unlock("order:123");

        verify(rLock).unlock();
    }

    @Test
    void shouldNotUnlockWhenNotHeldByCurrentThread() {
        doReturn(rLock).when(redissonClient).getLock(anyString());
        when(rLock.isHeldByCurrentThread()).thenReturn(false);

        distributedLock.unlock("order:123");

        verify(rLock, never()).unlock();
    }
}
