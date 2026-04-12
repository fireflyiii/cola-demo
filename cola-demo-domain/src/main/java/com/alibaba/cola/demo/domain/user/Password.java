package com.alibaba.cola.demo.domain.user;

/**
 * 密码值对象
 * 封装密码的领域概念，避免在领域层直接暴露明文密码
 */
public class Password {

    private final String encodedPassword;

    private Password(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    /**
     * 从已加密的密码创建
     */
    public static Password ofEncoded(String encodedPassword) {
        return new Password(encodedPassword);
    }

    /**
     * 获取已加密的密码值
     */
    public String getEncoded() {
        return encodedPassword;
    }
}
