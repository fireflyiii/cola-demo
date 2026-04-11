package com.alibaba.cola.demo.client.common;

import lombok.Getter;

/**
 * 业务错误码枚举
 */
@Getter
public enum BizErrorCode {

    B_CUSTOMER_NAME_NOT_BLANK("B_CUSTOMER_NAME_NOT_BLANK", "客户名称不能为空"),
    B_CUSTOMER_COMPANY_TYPE_NOT_BLANK("B_CUSTOMER_COMPANY_TYPE_NOT_BLANK", "公司类型不能为空"),
    B_USER_NOT_FOUND("B_USER_NOT_FOUND", "用户不存在"),
    B_USER_DISABLED("B_USER_DISABLED", "用户已被禁用"),
    B_ROLE_NOT_FOUND("B_ROLE_NOT_FOUND", "角色不存在"),
    B_PERMISSION_DENIED("B_PERMISSION_DENIED", "权限不足");

    private final String errCode;
    private final String errDesc;

    BizErrorCode(String errCode, String errDesc) {
        this.errCode = errCode;
        this.errDesc = errDesc;
    }
}
