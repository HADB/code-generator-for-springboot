# SpringBoot 代码生成器

## 说明

项目做多了之后，会发现有很多简单的增删改查的基础代码，没有技术含量，却要花费很多时间去写，浪费生命

于是有了这样一个脚本，根据数据库结构生成对应的 `Model`、`Mapper`、`Service`、`Controller` 等代码

代码基于 `Kotlin`，细节可根据个人喜好调整，目前是按照我的项目风格生成的

已集成小程序登录、密码登录、绑定手机号、微信支付相关内容

## 使用方式

注意，请使用 Python 3

#### 安装 `inflection`

```bash
$ pip install inflection
```

#### 项目执行命令
```bash
$ python3 main.py \
--group_id=${group_id} \
--artifact_id=${artifact_id} \
--version=${version} \
--port=${port} \
--package_name=${package_name} \
--project_path=${project_path} \
--description=${description} \
--registry_instance=${registry_instance} \
--registry_namespace=${registry_namespace} \
--registry_username=${registry_username} \
--registry_password=${registry_password}
```

首次执行会在 `package_path` 目录下初始化项目， `src/main/resources/sql` 文件夹内是 SQL 文件，新增文件后再次执行命令会自动更新代码，注意 SQL 文件名必须为：`t_xxx.sql`

#### SQL 文件示例
```sql
CREATE TABLE `t_shop` (
    `id`                    bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `number`                varchar(64)         NOT NULL                COMMENT '编号',
    `floor`                 tinyint(4)          NOT NULL                COMMENT '楼层',
    `location`              varchar(64)         DEFAULT NULL            COMMENT '位置',
    `gross_floor_area`      double(10,2)        DEFAULT NULL            COMMENT '建筑面积(单位:㎡)',
    `net_floor_area`        double(10,2)        DEFAULT NULL            COMMENT '使用面积(单位:㎡)',
    `create_time`           datetime            DEFAULT NULL            COMMENT '创建时间',
    `update_time`           datetime            DEFAULT NULL            COMMENT '更新时间',
    `is_delete`             tinyint(4)          NOT NULL DEFAULT '0'    COMMENT '是否删除(0:否, 1:是)',
    PRIMARY KEY                             (`id`),
    KEY         `idx_number`                (`number`),
    KEY         `idx_floor`                 (`floor`),
    KEY         `idx_create_time`           (`create_time`),
    KEY         `idx_update_time`           (`update_time`),
    KEY         `idx_is_delete`             (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商铺';
```

建议自动生成的代码放到单独的分支管理，每次生成后合并到主分支