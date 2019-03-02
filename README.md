# 生成 SpringBoot 基础的增删改查代码

## 说明

项目做多了之后，会发现有很多简单的增删改查的基础代码，没有技术含量，却要花费很多时间去写，浪费生命

于是有了这样一个脚本，根据数据库结构生成对应的 Model、Mapper、Service、Controller 等代码

代码基于 Kotlin，细节可根据个人喜好调整，目前是按照我的项目风格生成的

## 使用方式

安装`inflection`

```bash
pip install inflection
```

在`inputs`文件夹下放好 sql 文件，例如：

```sql
CREATE TABLE `t_credit_plan` (
    `id`                        bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`                      varchar(64)         NOT NULL                COMMENT '名称',
    `province_code`             varchar(8)          DEFAULT NULL            COMMENT '省份编码',
    `city_code`                 varchar(8)          DEFAULT NULL            COMMENT '城市编码',
    `district_code`             varchar(8)          DEFAULT NULL            COMMENT '区县编码',
    `min_amount`                int(11)             NOT NULL                COMMENT '最低充值金额(单位:分)',
    `give_away_amount`          int(11)             NOT NULL                COMMENT '赠送金额(单位:分)',
    `enable_time`               datetime            DEFAULT NULL            COMMENT '计划启用时间',
    `expire_time`               datetime            DEFAULT NULL            COMMENT '计划过期时间',
    `create_time`               datetime            DEFAULT NULL            COMMENT '创建时间',
    `update_time`               datetime            DEFAULT NULL            COMMENT '更新时间',
    `is_delete`                 tinyint(4)          NOT NULL DEFAULT '0'    COMMENT '是否删除(0:否, 1:是)',
    PRIMARY KEY                         (`id`),
    KEY         `idx_province_code`     (`province_code`),
    KEY         `idx_city_code`         (`city_code`),
    KEY         `idx_district_code`     (`district_code`),
    KEY         `idx_enable_time`       (`enable_time`),
    KEY         `idx_expire_time`       (`expire_time`),
    KEY         `idx_create_time`       (`create_time`),
    KEY         `idx_update_time`       (`update_time`),
    KEY         `idx_is_delete`         (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值计划';
```

执行 `python main.py -p demo.package.name`


输出文件在 outputs 文件夹下。
