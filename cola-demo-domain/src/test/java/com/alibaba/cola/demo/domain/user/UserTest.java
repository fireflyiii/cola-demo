package com.alibaba.cola.demo.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldBeEnabledWhenStatusIsOne() {
        User user = User.rebuild(null, "test", "encodedPwd", 1);

        assertTrue(user.isEnabled());
    }

    @Test
    void shouldNotBeEnabledWhenStatusIsZero() {
        User user = User.rebuild(null, "test", "encodedPwd", 0);

        assertFalse(user.isEnabled());
    }

    @Test
    void shouldNotBeEnabledWhenStatusIsNull() {
        User user = User.rebuild(null, "test", "encodedPwd", null);

        assertFalse(user.isEnabled());
    }
}
