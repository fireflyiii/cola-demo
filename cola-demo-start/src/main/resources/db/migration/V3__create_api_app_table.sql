-- API应用表
CREATE TABLE IF NOT EXISTS sys_api_app (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    app_name      VARCHAR(100) NOT NULL COMMENT '应用名称',
    api_key       VARCHAR(100) NOT NULL UNIQUE COMMENT 'API密钥',
    allowed_paths VARCHAR(500) NOT NULL COMMENT '允许访问路径(逗号分隔Ant模式)',
    expires_at    DATETIME DEFAULT NULL COMMENT '过期时间(NULL表示永不过期)',
    status        TINYINT DEFAULT 1 COMMENT '状态:0禁用 1启用',
    created_by    VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    created_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by    VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    updated_time  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT DEFAULT 0 COMMENT '是否删除:0否 1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API应用表';
