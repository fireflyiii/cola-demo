package com.alibaba.cola.demo.domain.customer;

/**
 * 公司类型值对象
 */
public enum CompanyType {

    STATE_OWNED("国有企业"),
    PRIVATE("民营企业"),
    FOREIGN("外资企业"),
    JOINT_VENTURE("合资企业"),
    OTHER("其他");

    private final String description;

    CompanyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static CompanyType fromCode(String code) {
        if (code == null) {
            return null;
        }
        try {
            return valueOf(code);
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}
