package com.alibaba.cola.demo.client.common;

import lombok.Getter;

/**
 * 领域异常
 * 所有领域层抛出的业务异常都应使用此类，配合 BizErrorCode 枚举保证错误码一致性
 */
@Getter
public class DomainException extends RuntimeException {

    private final String errCode;

    public DomainException(BizErrorCode errorCode) {
        super(errorCode.getErrDesc());
        this.errCode = errorCode.getErrCode();
    }

    public DomainException(BizErrorCode errorCode, Throwable cause) {
        super(errorCode.getErrDesc(), cause);
        this.errCode = errorCode.getErrCode();
    }

    public DomainException(String errCode, String message) {
        super(message);
        this.errCode = errCode;
    }

    public DomainException(String errCode, String message, Throwable cause) {
        super(message, cause);
        this.errCode = errCode;
    }
}
