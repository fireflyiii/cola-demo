package com.alibaba.cola.demo.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {

    @Test
    void shouldCreateFromEncodedPassword() {
        Password password = Password.ofEncoded("$2a$10$encodedPassword");

        assertEquals("$2a$10$encodedPassword", password.getEncoded());
    }

    @Test
    void shouldHandleEmptyEncodedPassword() {
        Password password = Password.ofEncoded("");

        assertEquals("", password.getEncoded());
    }
}
