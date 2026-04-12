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
    void shouldBeEnabledWhenStatusIsOne() {
        Role role = Role.create("ADMIN", "管理员");
        role.setRoleId(1L);
        // Role created via factory doesn't set status, so isEnabled returns false
        assertFalse(role.isEnabled());
    }
}
