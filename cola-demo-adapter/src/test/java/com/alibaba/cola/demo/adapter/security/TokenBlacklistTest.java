package com.alibaba.cola.demo.adapter.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TokenBlacklistTest {

    private TokenBlacklist tokenBlacklist;
    private RedissonClient redissonClient;

    @BeforeEach
    void setUp() {
        redissonClient = mock(RedissonClient.class);
        tokenBlacklist = new TokenBlacklist(redissonClient);
    }

    @Test
    void shouldNotContainTokenInitially() {
        RBucket<Object> bucket = mock(RBucket.class);
        doReturn(bucket).when(redissonClient).getBucket("token:blacklist:some-token");
        when(bucket.isExists()).thenReturn(false);

        assertFalse(tokenBlacklist.contains("some-token"));
    }

    @Test
    void shouldContainTokenAfterAdding() {
        RBucket<Object> bucket = mock(RBucket.class);
        doReturn(bucket).when(redissonClient).getBucket("token:blacklist:some-token");

        tokenBlacklist.add("some-token", 3600);

        verify(bucket).set(eq("1"), eq(Duration.ofSeconds(3600)));
    }

    @Test
    void shouldNotAddTokenWithZeroOrNegativeTTL() {
        RBucket<Object> bucket = mock(RBucket.class);
        doReturn(bucket).when(redissonClient).getBucket("token:blacklist:expired-token");

        tokenBlacklist.add("expired-token", 0);

        verify(bucket, never()).set(anyString(), any(Duration.class));
    }
}
