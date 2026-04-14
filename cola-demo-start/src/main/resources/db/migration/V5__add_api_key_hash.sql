-- 添加 API Key 哈希字段
ALTER TABLE `sys_api_app` ADD COLUMN `api_key_hash` VARCHAR(64) DEFAULT NULL COMMENT 'API Key SHA-256哈希值' AFTER `api_key`;

-- 为已有数据生成哈希值
UPDATE `sys_api_app` SET `api_key_hash` = SHA2(`api_key`, 256) WHERE `api_key` IS NOT NULL;

-- 为哈希字段创建唯一索引（后续查询通过哈希匹配）
CREATE UNIQUE INDEX `uk_api_key_hash` ON `sys_api_app`(`api_key_hash`);
