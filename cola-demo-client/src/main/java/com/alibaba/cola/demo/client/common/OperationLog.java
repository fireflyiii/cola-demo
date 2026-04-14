package com.alibaba.cola.demo.client.common;

import java.lang.annotation.*;

/**
 * 操作审计日志注解
 * 标注在需要审计的 Controller 方法上，AOP 切面自动记录操作人、操作类型、操作内容
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作类型（如：新增、修改、删除、分配、移除）
     */
    String type();

    /**
     * 操作描述
     */
    String description() default "";
}
