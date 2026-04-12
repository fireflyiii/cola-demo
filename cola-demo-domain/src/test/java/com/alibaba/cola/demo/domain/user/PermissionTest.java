package com.alibaba.cola.demo.domain.user;

import com.alibaba.cola.demo.client.common.DomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    void shouldCreatePermissionWhenValid() {
        Permission permission = Permission.create("customer:read", "客户查询", "API", "/customer");

        assertEquals("customer:read", permission.getPermissionCode());
        assertEquals(ResourceType.API, permission.getResourceType());
    }

    @Test
    void shouldThrowWhenPermissionCodeIsBlank() {
        assertThrows(DomainException.class, () -> Permission.create("", "查询", "API", "/test"));
    }

    @Test
    void shouldThrowWhenPermissionCodeIsNull() {
        assertThrows(DomainException.class, () -> Permission.create(null, "查询", "API", "/test"));
    }

    @Test
    void shouldDefaultToApiResourceTypeWhenNull() {
        Permission permission = Permission.create("test:read", "测试", null, "/test");

        assertEquals(ResourceType.API, permission.getResourceType());
    }
}
