CREATE TABLE `t_payment` (
    `id`                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`               BIGINT UNSIGNED NOT NULL                COMMENT '用户ID',
    `body`                  VARCHAR(128)                            COMMENT '商品描述',
    `detail`                TEXT                                    COMMENT '商品详情',
    `amount`                INT             NOT NULL DEFAULT 0      COMMENT '金额(单位:分)',
    `status`                TINYINT         NOT NULL DEFAULT 0      COMMENT '状态(0:已创建, 1:已预下单, 2:已取消, 3:已支付, 4:已退款)',
    `payment_type`          TINYINT         NOT NULL DEFAULT 0      COMMENT '支付类型(0:微信, 1:支付宝)',
    `wx_transaction_id`     VARCHAR(64)                             COMMENT '微信支付订单号',
    `wx_out_trade_no`       VARCHAR(64)                             COMMENT '微信商户订单号',
    `wx_payment_open_id`    VARCHAR(64)                             COMMENT '微信支付的用户 OpenId',
    `message`               VARCHAR(64)                             COMMENT '支付返回消息',
    `prepay_time`           DATETIME                                COMMENT '预下单时间',
    `canceled_time`         DATETIME                                COMMENT '取消时间',
    `payment_time`          DATETIME                                COMMENT '支付时间',
    `refund_time`           DATETIME                                COMMENT '退款时间',
    `created_time`          DATETIME                                COMMENT '创建时间',
    `updated_time`          DATETIME                                COMMENT '更新时间',
    `is_delete`             TINYINT         NOT NULL DEFAULT 0      COMMENT '是否删除(0:否, 1:是)',
    PRIMARY KEY (`id`),
    KEY         `idx_user_id`               (`user_id`),
    KEY         `idx_wx_transaction_id`     (`wx_transaction_id`),
    KEY         `idx_wx_out_trade_no`       (`wx_out_trade_no`),
    KEY         `idx_status`                (`status`),
    KEY         `idx_created_time`          (`created_time`),
    KEY         `idx_updated_time`          (`updated_time`),
    KEY         `idx_is_delete`             (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付';