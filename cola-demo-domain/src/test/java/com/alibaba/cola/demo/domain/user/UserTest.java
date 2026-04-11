package com.alibaba.cola.demo.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldBeEnabledWhenStatusIsOne() {
        User user = new User();
        user.setStatus(1);

        assertTrue(user.isEnabled());
    }

    @Test
    void shouldNotBeEnabledWhenStatusIsZero() {
        User user = new User();
        user.setStatus(0);

        assertFalse(user.isEnabled());
    }

    @Test
    void shouldNotBeEnabledWhenStatusIsNull() {
        User user = new User();

        assertFalse(user.isEnabled());
    }
}
