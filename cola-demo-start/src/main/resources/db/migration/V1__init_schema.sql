-- 客户表
CREATE TABLE IF NOT EXISTS customer (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(64)  NOT NULL,
    company_type  VARCHAR(32),
    created_by    VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    created_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by    VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    updated_time  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT DEFAULT 0 COMMENT '是否删除:0否 1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
