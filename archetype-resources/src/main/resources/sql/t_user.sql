CREATE TABLE `t_user` (
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT     COMMENT '主键',
    `mobile`            VARCHAR(32)                                 COMMENT '手机号',
    `open_id`           VARCHAR(64)                                 COMMENT '微信openid',
    `nickname`          VARCHAR(64)                                 COMMENT '微信昵称',
    `username`          VARCHAR(64)                                 COMMENT '用户名',
    `password`          VARCHAR(64)                                 COMMENT '密码',
    `salt`              VARCHAR(64)                                 COMMENT '盐',
    `avatar_url`        TEXT                                        COMMENT '微信头像url',
    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    `update_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '更新时间',
    `is_delete`         TINYINT         NOT NULL DEFAULT 0          COMMENT '是否删除',
    PRIMARY KEY                     (`id`),
    UNIQUE KEY  `idx_mobile`        (`mobile`),
    UNIQUE KEY  `idx_open_id`       (`open_id`),
    UNIQUE KEY  `idx_username`      (`username`),
    KEY         `idx_create_time`   (`create_time`),
    KEY         `idx_update_time`   (`update_time`),
    KEY         `idx_is_delete`     (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';
