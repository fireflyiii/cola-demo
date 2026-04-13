package com.alibaba.cola.demo.client.dto.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApiAppDTO extends DTO {

    private Long id;
    private String appName;
    private String apiKey;
    private String allowedPaths;
    private LocalDateTime expiresAt;
    private Integer status;
}
