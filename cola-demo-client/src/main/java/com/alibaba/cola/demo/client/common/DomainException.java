package com.alibaba.cola.demo.client.common;

import lombok.Getter;

/**
 * 领域异常
 * 所有领域层抛出的业务异常都应使用此类，配合 BizErrorCode 枚举保证错误码一致性
 * 支持通过 ErrorCodeResolver 实现 i18n 消息解析
 */
@Getter
public class DomainException extends RuntimeException {

    private final String errCode;

    public DomainException(BizErrorCode errorCode) {
        super(resolveMessage(errorCode));
        this.errCode = errorCode.getErrCode();
    }

    public DomainException(BizErrorCode errorCode, Throwable cause) {
        super(resolveMessage(errorCode), cause);
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

    /**
     * 解析 BizErrorCode 的本地化消息
     * 优先使用 ErrorCodeResolver（i18n），兜底使用 errDesc
     */
    private static String resolveMessage(BizErrorCode errorCode) {
        ErrorCodeResolver resolver = ErrorCodeResolverHolder.getResolver();
        if (resolver != null) {
            return resolver.resolve(errorCode.getErrCode());
        }
        return errorCode.getErrDesc();
    }
}
