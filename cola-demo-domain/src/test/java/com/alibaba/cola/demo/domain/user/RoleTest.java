package com.alibaba.cola.demo.domain.user;

import com.alibaba.cola.demo.client.common.DomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void shouldCreateRoleWhenValid() {
        Role role = Role.create("ADMIN", "管理员");

        assertEquals("ADMIN", role.getRoleCode());
        assertEquals("管理员", role.getRoleName());
        assertNull(role.getRoleId());
    }

    @Test
    void shouldThrowWhenRoleCodeIsBlank() {
        assertThrows(DomainException.class, () -> Role.create("", "管理员"));
    }

    @Test
    void shouldThrowWhenRoleCodeIsNull() {
        assertThrows(DomainException.class, () -> Role.create(null, "管理员"));
    }

    @Test
    void shouldThrowWhenRoleNameIsBlank() {
        assertThrows(DomainException.class, () -> Role.create("ADMIN", ""));
    }

    @Test
    void shouldRebuildRoleWithStatus() {
        Role role = Role.rebuild(1L, "ADMIN", "管理员", 1);

        assertEquals(1L, role.getRoleId());
        assertTrue(role.isEnabled());
    }

    @Test
    void shouldBeEnabledWhenStatusIsOne() {
        Role role = Role.rebuild(1L, "ADMIN", "管理员", 1);
        assertTrue(role.isEnabled());
    }

    @Test
    void shouldNotBeEnabledWhenStatusIsZero() {
        Role role = Role.rebuild(1L, "ADMIN", "管理员", 0);
        assertFalse(role.isEnabled());
    }
}
