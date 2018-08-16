# 生成 SpringBoot 基础的增删改查代码

## 说明

项目做多了之后，会发现有很多简单的增删改查的基础代码，没有技术含量，却要花费很多时间去写，浪费青春

于是有了这样一个脚本，根据数据库结构生成对应的 Model、Mapper、Service、Controller 等代码

代码基于 Kotlin，细节可根据个人喜好调整，目前是按照我的项目风格生成的

## 使用方式

安装`inflection`

```bash
pip install inflection
```

在`inputs`文件夹下放好 sql 文件（只需要列的部分），例如：

```sql
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
`machine_code` varchar(64) NOT NULL COMMENT '机器码',
`description` varchar(128) DEFAULT NULL COMMENT '机器描述',
`communication_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '通讯状态(0:未连接, 1:已连接)',
`industry_id` bigint(20) DEFAULT NULL COMMENT '行业ID',
`province_code` varchar(8) DEFAULT NULL COMMENT '省编码',
`province_name` varchar(32) DEFAULT NULL COMMENT '省名',
`city_code` varchar(8) DEFAULT NULL COMMENT '城市编码',
`city_name` varchar(32) DEFAULT NULL COMMENT '城市名',
`district_code` varchar(8) DEFAULT NULL COMMENT '区编码',
`district_name` varchar(32) DEFAULT NULL COMMENT '区名',
`street_code` varchar(8) DEFAULT NULL COMMENT '街道编码',
`street_name` varchar(32) DEFAULT NULL COMMENT '街道名',
`specific_address` varchar(128) DEFAULT NULL COMMENT '具体地址',
`maintainer_name` varchar(32) DEFAULT NULL COMMENT '维护人姓名',
`maintainer_mobile` varchar(11) DEFAULT NULL COMMENT '维护人手机号',
`peak_times` text COMMENT '人群高峰时间(JSON数组)',
`enable_time_from` time DEFAULT NULL COMMENT '启用时间(开始)',
`enable_time_to` time DEFAULT NULL COMMENT '启用时间(结束)',
`heartbeat_time` datetime DEFAULT NULL COMMENT '心跳时间',
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(0:否, 1:是)',
```

执行 `python main.py`

输出文件在 outputs 文件夹下。
