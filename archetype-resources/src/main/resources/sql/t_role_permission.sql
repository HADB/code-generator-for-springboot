CREATE TABLE `t_role_permission` (
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT     COMMENT '主键',
    `role_id`       BIGINT UNSIGNED NOT NULL                    COMMENT '角色ID',
    `permission_id` BIGINT UNSIGNED NOT NULL                    COMMENT '权限ID',
    `create_time`   DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    `update_time`   DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '更新时间',
    `is_delete`     TINYINT         NOT NULL DEFAULT 0          COMMENT '是否删除',
    PRIMARY KEY                     (`id`),
    KEY         `idx_role_id`       (`role_id`),
    KEY         `idx_permission_id` (`permission_id`),
    KEY         `idx_create_time`   (`create_time`),
    KEY         `idx_update_time`   (`update_time`),
    KEY         `idx_is_delete`     (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色-权限';