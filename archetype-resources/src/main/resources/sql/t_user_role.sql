CREATE TABLE `t_user_role` (
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT     COMMENT '主键',
    `user_id`       BIGINT UNSIGNED NOT NULL                    COMMENT '用户ID',
    `role_id`       BIGINT UNSIGNED NOT NULL                    COMMENT '角色ID',
    `create_time`   DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    `update_time`   DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '更新时间',
    `is_delete`     TINYINT         NOT NULL DEFAULT 0          COMMENT '是否删除',
    PRIMARY KEY                     (`id`),
    KEY         `idx_user_id`       (`user_id`),
    KEY         `idx_role_id`       (`role_id`),
    KEY         `idx_create_time`   (`create_time`),
    KEY         `idx_update_time`   (`update_time`),
    KEY         `idx_is_delete`     (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色';