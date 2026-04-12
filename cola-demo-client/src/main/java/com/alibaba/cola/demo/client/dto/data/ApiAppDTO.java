package com.alibaba.cola.demo.client.dto.data;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiAppDTO {

    private Long id;
    private String appName;
    private String apiKey;
    private String allowedPaths;
    private LocalDateTime expiresAt;
    private Integer status;
}
