-- 订单表
CREATE TABLE IF NOT EXISTS t_order (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_name    VARCHAR(100) NOT NULL COMMENT '订单名称',
    amount        DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户名称',
    status        VARCHAR(20) NOT NULL DEFAULT 'CREATED' COMMENT '订单状态',
    created_by    VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    created_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by    VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    updated_time  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT DEFAULT 0 COMMENT '是否删除:0否 1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
