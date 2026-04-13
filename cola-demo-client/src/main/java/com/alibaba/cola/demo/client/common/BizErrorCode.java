package com.alibaba.cola.demo.client.common;

import lombok.Getter;

/**
 * 业务错误码枚举
 * 所有领域层抛出 DomainException 时应使用此枚举保证错误码一致性
 * errCode 直接使用枚举名称，避免冗余
 */
@Getter
public enum BizErrorCode {

    // Customer
    B_CUSTOMER_NAME_NOT_BLANK("客户名称不能为空"),
    B_CUSTOMER_COMPANY_TYPE_NOT_BLANK("公司类型不能为空"),

    // User
    B_USER_NOT_FOUND("用户不存在"),
    B_USER_DISABLED("用户已被禁用"),

    // Role
    B_ROLE_CODE_NOT_BLANK("角色编码不能为空"),
    B_ROLE_NAME_NOT_BLANK("角色名称不能为空"),
    B_ROLE_NOT_FOUND("角色不存在"),

    // Permission
    B_PERMISSION_CODE_NOT_BLANK("权限编码不能为空"),
    B_PERMISSION_NAME_NOT_BLANK("权限名称不能为空"),

    // Order
    B_ORDER_NAME_NOT_BLANK("订单名称不能为空"),
    B_ORDER_AMOUNT_INVALID("订单金额无效"),

    // ApiApp
    B_API_APP_NAME_NOT_BLANK("应用名称不能为空"),
    B_API_APP_NOT_FOUND("API应用不存在"),
    B_API_APP_DISABLED("API应用已被禁用"),
    B_API_APP_EXPIRED("API应用已过期"),
    B_API_APP_PATH_NOT_ALLOWED("API应用无权访问该路径"),

    // Common
    B_PERMISSION_DENIED("权限不足");

    private final String errDesc;

    BizErrorCode(String errDesc) {
        this.errDesc = errDesc;
    }

    /**
     * 错误码直接使用枚举名称，避免冗余字段
     */
    public String getErrCode() {
        return name();
    }
}
