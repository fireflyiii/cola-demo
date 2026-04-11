package com.alibaba.cola.demo.adapter.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenBlacklistTest {

    private TokenBlacklist tokenBlacklist;

    @BeforeEach
    void setUp() {
        tokenBlacklist = new TokenBlacklist();
    }

    @Test
    void shouldNotContainTokenInitially() {
        assertFalse(tokenBlacklist.contains("some-token"));
    }

    @Test
    void shouldContainTokenAfterAdding() {
        long futureExpire = System.currentTimeMillis() + 3600000;
        tokenBlacklist.add("some-token", futureExpire);

        assertTrue(tokenBlacklist.contains("some-token"));
    }

    @Test
    void shouldNotContainExpiredBlacklistEntry() {
        long pastExpire = System.currentTimeMillis() - 1000;
        tokenBlacklist.add("some-token", pastExpire);

        assertFalse(tokenBlacklist.contains("some-token"));
    }
}
