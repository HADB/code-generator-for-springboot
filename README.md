# SpringBoot 代码生成器

## 说明

项目做多了之后，会发现有很多简单的增删改查的基础代码，没有技术含量，却要花费很多时间去写，浪费生命

于是有了这样一个脚本，根据数据库结构生成对应的 `Model`、`Mapper`、`Service`、`Controller` 等代码

代码基于 `Kotlin`，细节可根据个人喜好调整，目前是按照我的项目风格生成的

已集成小程序登录、密码登录、绑定手机号、微信支付相关内容

## 使用方式

注意，请使用 Python 3，另外建议开启 Idea -> Preferences -> Editor -> Code Style -> Kotlin -> Other -> Use trailing comma，以减少手动添加字段导致的合并冲突

#### 安装依赖（使用 [uv](https://docs.astral.sh/uv/)）

```bash
$ uv venv # 安装虚拟环境
$ uv sync # 安装项目依赖
$ source .venv/bin/activate # 激活虚拟环境
```

### 设置环境变量

在 `code-generator-for-springboot` 目录下执行以下命令

```bash
$ echo "export CODE_GENERATOR_PATH=\"$(pwd)\"" >> ~/.zshrc
$ source ~/.zshrc
```

#### 创建项目

```bash
$ uv run main.py init
```

#### 更新项目

```bash
$ cd "/path/to/your/project"
$ ./generator.sh
```

首次执行会在 `package_path` 目录下初始化项目， `src/main/resources/ddl` 文件夹内是 SQL 文件，新增文件后执行命令会自动更新代码，注意 SQL 文件名必须为：`t_xxx.sql`

#### SQL 文件示例

```sql
CREATE TABLE `t_shop` (
    `id`                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `number`                VARCHAR(64)     NOT NULL                COMMENT '编号',
    `floor`                 TINYINT         NOT NULL                COMMENT '楼层',
    `location`              VARCHAR(64)                             COMMENT '位置',
    `gross_floor_area`      DOUBLE(10,2)                            COMMENT '建筑面积(单位:㎡)',
    `net_floor_area`        DOUBLE(10,2)                            COMMENT '使用面积(单位:㎡)',
    `price`                 DECIMAL(10,2)                           COMMENT '单价(单位:元)',
    `create_time`           DATETIME                                COMMENT '创建时间',
    `update_time`           DATETIME                                COMMENT '更新时间',
    PRIMARY KEY                             (`id`),
    KEY         `idx_number`                (`number`),
    KEY         `idx_floor`                 (`floor`),
    KEY         `idx_create_time`           (`create_time`),
    KEY         `idx_update_time`           (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_zh_0900_as_cs COMMENT='商铺';
```

自动生成的代码会自动放到 `generate` 分支管理，业务代码请在主分支提交，每次自动生成后将 `generate` 分支合并到主分支，这样可减少合并冲突
