CREATE TABLE `t_role` (
    `id`            BIGINT(20) UNSIGNED     NOT NULL AUTO_INCREMENT     COMMENT '主键',
    `key`           VARCHAR(64)             NOT NULL                    COMMENT '角色标识',
    `name`          VARCHAR(64)             NOT NULL                    COMMENT '角色名称',
    `description`   VARCHAR(128)            DEFAULT NULL                COMMENT '角色描述',
    `built_in`      TINYINT(4)              NOT NULL DEFAULT '0'        COMMENT '是否内置',
    `create_time`   DATETIME                DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    `update_time`   DATETIME                DEFAULT CURRENT_TIMESTAMP   COMMENT '更新时间',
    `is_delete`     TINYINT(4)              NOT NULL DEFAULT '0'        COMMENT '是否删除',
    PRIMARY KEY                     (`id`),
    UNIQUE KEY  `idx_key`           (`key`),
    KEY         `idx_create_time`   (`create_time`),
    KEY         `idx_update_time`   (`update_time`),
    KEY         `idx_is_delete`     (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色';