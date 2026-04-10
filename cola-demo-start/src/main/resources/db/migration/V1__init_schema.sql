-- customer table
CREATE TABLE IF NOT EXISTS customer (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(64)  NOT NULL,
    company_type  VARCHAR(32)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;