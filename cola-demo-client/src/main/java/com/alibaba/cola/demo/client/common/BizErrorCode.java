package com.alibaba.cola.demo.client.common;

import lombok.Getter;

@Getter
public enum BizErrorCode {

    B_CUSTOMER_NAME_NOT_BLANK("B_CUSTOMER_NAME_NOT_BLANK", "客户名称不能为空"),
    B_CUSTOMER_COMPANY_TYPE_NOT_BLANK("B_CUSTOMER_COMPANY_TYPE_NOT_BLANK", "公司类型不能为空");

    private final String errCode;
    private final String errDesc;

    BizErrorCode(String errCode, String errDesc) {
        this.errCode = errCode;
        this.errDesc = errDesc;
    }
}
