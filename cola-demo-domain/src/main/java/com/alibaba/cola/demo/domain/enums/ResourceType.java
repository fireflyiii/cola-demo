package com.alibaba.cola.demo.domain.enums;

/**
 * 资源类型值对象
 */
public enum ResourceType {

    MENU("菜单"),
    BUTTON("按钮"),
    API("接口");

    private final String description;

    ResourceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
