CREATE TABLE `t_flyway_history` (
    `installed_rank` INT                                 NOT NULL,
    `version`        VARCHAR(50)                         NULL,
    `description`    VARCHAR(200)                        NOT NULL,
    `type`           VARCHAR(20)                         NOT NULL,
    `script`         VARCHAR(1000)                       NOT NULL,
    `checksum`       INT                                 NULL,
    `installed_by`   VARCHAR(100)                        NOT NULL,
    `installed_on`   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `execution_time` INT                                 NOT NULL,
    `success`        TINYINT(1)                          NOT NULL,
    PRIMARY KEY (`installed_rank`),
    KEY `t_flyway_history_s_idx` (`success`)
) ENGINE = InnoDB DEFAULT CHARSET = `utf8mb4`;
