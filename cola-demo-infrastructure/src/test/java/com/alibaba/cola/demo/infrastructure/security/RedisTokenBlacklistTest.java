package com.alibaba.cola.demo.infrastructure.security;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RedisTokenBlacklist 单元测试
 * 重点验证 SHA-256 哈希逻辑的正确性
 */
class RedisTokenBlacklistTest {

    private final RedisTokenBlacklist tokenBlacklist = new RedisTokenBlacklist(null);

    @Test
    void shouldHashTokenConsistently() throws Exception {
        Method sha256Method = RedisTokenBlacklist.class.getDeclaredMethod("sha256", String.class);
        sha256Method.setAccessible(true);

        String hash1 = (String) sha256Method.invoke(tokenBlacklist, "test-token");
        String hash2 = (String) sha256Method.invoke(tokenBlacklist, "test-token");

        assertEquals(hash1, hash2);
        assertEquals(64, hash1.length());
    }

    @Test
    void shouldProduceCorrectSha256() throws Exception {
        Method sha256Method = RedisTokenBlacklist.class.getDeclaredMethod("sha256", String.class);
        sha256Method.setAccessible(true);

        String hash = (String) sha256Method.invoke(tokenBlacklist, "test-token");

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] expected = digest.digest("test-token".getBytes(StandardCharsets.UTF_8));
        StringBuilder expectedHex = new StringBuilder();
        for (byte b : expected) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) expectedHex.append('0');
            expectedHex.append(hex);
        }

        assertEquals(expectedHex.toString(), hash);
    }

    @Test
    void shouldProduceDifferentHashesForDifferentTokens() throws Exception {
        Method sha256Method = RedisTokenBlacklist.class.getDeclaredMethod("sha256", String.class);
        sha256Method.setAccessible(true);

        String hash1 = (String) sha256Method.invoke(tokenBlacklist, "token-A");
        String hash2 = (String) sha256Method.invoke(tokenBlacklist, "token-B");

        assertNotEquals(hash1, hash2);
    }

    @Test
    void shouldNotAddWhenRemainingSecondsZeroOrNegative() {
        // remainingSeconds <= 0 时直接 return，不访问 redissonClient
        assertDoesNotThrow(() -> {
            tokenBlacklist.add("token", 0);
            tokenBlacklist.add("token", -1);
        });
    }
}
