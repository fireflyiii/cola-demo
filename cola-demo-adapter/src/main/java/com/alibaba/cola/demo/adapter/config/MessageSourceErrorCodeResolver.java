package com.alibaba.cola.demo.adapter.config;

import com.alibaba.cola.demo.client.common.ErrorCodeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 错误码消息解析实现
 * 基于 Spring MessageSource 实现 i18n
 * 消息 key 为 BizErrorCode 枚举名称（如 B_CUSTOMER_NAME_NOT_BLANK）
 */
@Component
@RequiredArgsConstructor
public class MessageSourceErrorCodeResolver implements ErrorCodeResolver {

    private final MessageSource messageSource;

    @Override
    public String resolve(String errorCode, Locale locale) {
        Locale targetLocale = locale != null ? locale : LocaleContextHolder.getLocale();
        return messageSource.getMessage(errorCode, null, errorCode, targetLocale);
    }
}
