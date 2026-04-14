package com.alibaba.cola.demo.client.common;

import java.util.Locale;

/**
 * 错误码消息解析接口
 * 定义在 Client 层，Adapter 层提供实现
 * 通过 Spring MessageSource 实现 i18n 消息解析
 */
public interface ErrorCodeResolver {

    /**
     * 根据错误码获取本地化消息
     *
     * @param errorCode 错误码（如 BizErrorCode 枚举名称）
     * @param locale    区域（可为 null，使用默认 Locale）
     * @return 本地化错误消息
     */
    String resolve(String errorCode, Locale locale);

    /**
     * 根据错误码获取默认 Locale 的消息
     */
    default String resolve(String errorCode) {
        return resolve(errorCode, null);
    }
}
