CREATE TABLE `t_permission` (
    `id`            BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT     COMMENT '主键',
    `key`           VARCHAR(64)         NOT NULL                    COMMENT '权限标识',
    `name`          VARCHAR(64)         NOT NULL                    COMMENT '权限名称',
    `description`   VARCHAR(128)                                    COMMENT '权限描述',
    `type`          TINYINT             NOT NULL DEFAULT 0          COMMENT '权限类型(0:API权限,1:菜单权限)',
    `api_path`      VARCHAR(128)                                    COMMENT 'API 路径',
    `api_method`    VARCHAR(16)                                     COMMENT 'API 方法',
    `create_time`   DATETIME            DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    `update_time`   DATETIME            DEFAULT CURRENT_TIMESTAMP   COMMENT '更新时间',
    PRIMARY KEY                     (`id`),
    UNIQUE KEY  `idx_key`           (`key`),
    KEY         `idx_type`          (`type`),
    KEY         `idx_create_time`   (`create_time`),
    KEY         `idx_update_time`   (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限';
