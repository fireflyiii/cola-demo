package com.alibaba.cola.demo.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * API应用实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_api_app")
public class ApiAppEntity extends BaseEntity {

    private String appName;
    private String apiKey;
    private String apiKeyHash;
    private String allowedPaths;
    private LocalDateTime expiresAt;
    private Integer status;
}
