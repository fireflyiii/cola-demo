package com.alibaba.cola.demo.adapter.security;

import com.alibaba.cola.demo.domain.common.TokenBlacklist;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TokenBlacklist 接口契约测试
 * 具体实现在 Infrastructure 层测试
 */
class TokenBlacklistTest {

    @Test
    void shouldDefineAddAndContainsMethods() {
        // 验证接口方法存在
        TokenBlacklist blacklist = new TokenBlacklist() {
            @Override
            public void add(String token, long remainingSeconds) {
            }

            @Override
            public boolean contains(String token) {
                return false;
            }
        };

        assertDoesNotThrow(() -> blacklist.add("token", 3600));
        assertFalse(blacklist.contains("token"));
    }
}
