# 生成 SpringBoot 基础的增删改查代码

## 说明

项目做多了之后，会发现有很多简单的增删改查的代码，没有技术含量，却要花费很多时间去写，浪费青春

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

输出文件在 outputs 文件夹下，示例效果如下：

`Device.kt`文件

```kotlin
package run.monkey.op.xiaotong.models

import run.monkey.op.xiaotong.annotations.NoArg
import java.util.*

@NoArg
data class Device(
        val id: Long = 0,                   // 主键
        val machineCode: String,            // 机器码
        val description: String?,           // 机器描述
        val communicationStatus: Int,       // 通讯状态(0:未连接, 1:已连接)
        val industryId: Long?,              // 行业ID
        val provinceCode: String?,          // 省编码
        val provinceName: String?,          // 省名
        val cityCode: String?,              // 城市编码
        val cityName: String?,              // 城市名
        val districtCode: String?,          // 区编码
        val districtName: String?,          // 区名
        val streetCode: String?,            // 街道编码
        val streetName: String?,            // 街道名
        val specificAddress: String?,       // 具体地址
        val maintainerName: String?,        // 维护人姓名
        val maintainerMobile: String?,      // 维护人手机号
        val peakTimes: String?,             // 人群高峰时间(JSON数组)
        val enableTimeFrom: String?,        // 启用时间(开始)
        val enableTimeTo: String?,          // 启用时间(结束)
        val heartbeatTime: Date?,           // 心跳时间
        val createTime: Date? = Date(0),    // 创建时间
        val updateTime: Date? = Date(0),    // 更新时间
        val isDelete: Int = 0               // 是否删除(0:否, 1:是)
)
```

`DeviceController.kt`文件

```kotlin
package run.monkey.op.xiaotong.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import run.monkey.op.xiaotong.annotations.AllowAnonymous
import run.monkey.op.xiaotong.annotations.Permission
import run.monkey.op.xiaotong.models.Response
import run.monkey.op.xiaotong.services.DeviceService
import run.monkey.op.xiaotong.viewmodels.common.SearchResponse
import run.monkey.op.xiaotong.viewmodels.device.DeviceEditRequest
import run.monkey.op.xiaotong.viewmodels.device.DeviceSearchRequest

@CrossOrigin
@RestController
@RequestMapping("/device")
class DeviceController {
    @Autowired
    lateinit var deviceService: DeviceService

    @PostMapping
    @Permission
    fun add(@RequestBody request: DeviceEditRequest): Response {
        deviceService.editDevice(request)
        return Response.success()
    }

    @PutMapping("/{id}")
    @Permission
    fun edit(@PathVariable("id") id: Long, @RequestBody request: DeviceEditRequest): Response {
        request.id = id
        deviceService.editDevice(request)
        return Response.success()
    }

    @DeleteMapping("/{id}")
    @Permission
    fun delete(@PathVariable("id") id: Long): Response {
        deviceService.deleteDevice(id)
        return Response.success()
    }

    @GetMapping("/all")
    @AllowAnonymous
    fun all(): Response {
        val result = deviceService.getAllDevices()
        return Response.success(result)
    }

    @PostMapping("/search")
    @AllowAnonymous
    fun search(@RequestBody request: DeviceSearchRequest): Response {
        val results = deviceService.searchPagingDevices(request)
        val count = deviceService.searchPagingDevicesCount(request)
        val response = SearchResponse(results, count)
        return Response.success(response)
    }
}
```

`DeviceService.kt`文件

```kotlin
package run.monkey.op.xiaotong.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import run.monkey.op.xiaotong.mappers.DeviceMapper
import run.monkey.op.xiaotong.models.Device
import run.monkey.op.xiaotong.viewmodels.device.DeviceEditRequest
import run.monkey.op.xiaotong.viewmodels.device.DeviceSearchRequest

@Component
open class DeviceService {
    @Autowired
    private lateinit val deviceMapper: DeviceMapper

    fun editDevice(request: DeviceEditRequest): Long {
        val device = Device(
                id = request.id
                machineCode = request.machineCode
                description = request.description
                communicationStatus = request.communicationStatus
                industryId = request.industryId
                provinceCode = request.provinceCode
                provinceName = request.provinceName
                cityCode = request.cityCode
                cityName = request.cityName
                districtCode = request.districtCode
                districtName = request.districtName
                streetCode = request.streetCode
                streetName = request.streetName
                specificAddress = request.specificAddress
                maintainerName = request.maintainerName
                maintainerMobile = request.maintainerMobile
                peakTimes = request.peakTimes
                enableTimeFrom = request.enableTimeFrom
                enableTimeTo = request.enableTimeTo
                heartbeatTime = request.heartbeatTime
        )
        if (device.id == 0L) {
            deviceMapper.insertDevice(device)
        } else {
            deviceMapper.updateDevice(device)
        }
        return device.id
    }

    fun deleteDevice(id: Long) {
        deviceMapper.deleteDevice(id)
    }

    fun getAllDevices(): List<Device> {
        return deviceMapper.selectAllDevices()
    }

    fun searchPagingDevices(deviceSearchRequest: DeviceSearchRequest): List<Device> {
        return deviceMapper.selectPagingDevices(deviceSearchRequest)
    }

    fun searchPagingDevicesCount(deviceSearchRequest: DeviceSearchRequest): Long {
        return deviceMapper.selectPagingDevicesCount(deviceSearchRequest)
    }
}
```

`DeviceEditRequest.kt`文件

```kotlin
package run.monkey.op.xiaotong.models

import run.monkey.op.xiaotong.annotations.NoArg
import java.util.*

@NoArg
data class Device(
        val id: Long = 0,                 // 主键
        val machineCode: String,          // 机器码
        val description: String?,         // 机器描述
        val communicationStatus: Int,     // 通讯状态(0:未连接, 1:已连接)
        val industryId: Long?,            // 行业ID
        val provinceCode: String?,        // 省编码
        val provinceName: String?,        // 省名
        val cityCode: String?,            // 城市编码
        val cityName: String?,            // 城市名
        val districtCode: String?,        // 区编码
        val districtName: String?,        // 区名
        val streetCode: String?,          // 街道编码
        val streetName: String?,          // 街道名
        val specificAddress: String?,     // 具体地址
        val maintainerName: String?,      // 维护人姓名
        val maintainerMobile: String?,    // 维护人手机号
        val peakTimes: String?,           // 人群高峰时间(JSON数组)
        val enableTimeFrom: String?,      // 启用时间(开始)
        val enableTimeTo: String?,        // 启用时间(结束)
        val heartbeatTime: Date?          // 心跳时间
)
```

`DeviceMapper.kt`文件

```kotlin
package run.monkey.op.xiaotong.mappers

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import run.monkey.op.xiaotong.models.Device
import run.monkey.op.xiaotong.viewmodels.device.DeviceSearchRequest

@Mapper
interface DeviceMapper {
    fun insertDevice(@Param("device") device: Device)
    fun updateDevice(@Param("device") device: Device)
    fun deleteDevice(@Param("id") id: Long)
    fun selectAllDevices(): List<Device>
    fun selectPagingDevices(@Param("request") deviceSearchRequest: DeviceSearchRequest): List<Device>
    fun selectPagingDevicesCount(@Param("request") deviceSearchRequest: DeviceSearchRequest): Long
}
```

`DeviceMapper.xml`文件

```xml
<sql id="deviceColumns">
    `id` as `id`,
    `machine_code` as `machineCode`,
    `description` as `description`,
    `communication_status` as `communicationStatus`,
    `industry_id` as `industryId`,
    `province_code` as `provinceCode`,
    `province_name` as `provinceName`,
    `city_code` as `cityCode`,
    `city_name` as `cityName`,
    `district_code` as `districtCode`,
    `district_name` as `districtName`,
    `street_code` as `streetCode`,
    `street_name` as `streetName`,
    `specific_address` as `specificAddress`,
    `maintainer_name` as `maintainerName`,
    `maintainer_mobile` as `maintainerMobile`,
    `peak_times` as `peakTimes`,
    `enable_time_from` as `enableTimeFrom`,
    `enable_time_to` as `enableTimeTo`,
    `heartbeat_time` as `heartbeatTime`,
    `create_time` as `createTime`,
    `update_time` as `updateTime`,
    `is_delete` as `isDelete`
</sql>

<insert id="insertDevice">
    INSERT INTO `t_device`(
    `machine_code`,
    `description`,
    `communication_status`,
    `industry_id`,
    `province_code`,
    `province_name`,
    `city_code`,
    `city_name`,
    `district_code`,
    `district_name`,
    `street_code`,
    `street_name`,
    `specific_address`,
    `maintainer_name`,
    `maintainer_mobile`,
    `peak_times`,
    `enable_time_from`,
    `enable_time_to`,
    `heartbeat_time`,
    `create_time`,
    `update_time`)
    VALUES(
    #{device.machineCode},
    #{device.description},
    #{device.communicationStatus},
    #{device.industryId},
    #{device.provinceCode},
    #{device.provinceName},
    #{device.cityCode},
    #{device.cityName},
    #{device.districtCode},
    #{device.districtName},
    #{device.streetCode},
    #{device.streetName},
    #{device.specificAddress},
    #{device.maintainerName},
    #{device.maintainerMobile},
    #{device.peakTimes},
    #{device.enableTimeFrom},
    #{device.enableTimeTo},
    #{device.heartbeatTime},
    NOW(),
    NOW())
</insert>

<update id="updateDevice">
    UPDATE `device` SET
    `machine_code` = #{device.machineCode},
    `description` = #{device.description},
    `communication_status` = #{device.communicationStatus},
    `industry_id` = #{device.industryId},
    `province_code` = #{device.provinceCode},
    `province_name` = #{device.provinceName},
    `city_code` = #{device.cityCode},
    `city_name` = #{device.cityName},
    `district_code` = #{device.districtCode},
    `district_name` = #{device.districtName},
    `street_code` = #{device.streetCode},
    `street_name` = #{device.streetName},
    `specific_address` = #{device.specificAddress},
    `maintainer_name` = #{device.maintainerName},
    `maintainer_mobile` = #{device.maintainerMobile},
    `peak_times` = #{device.peakTimes},
    `enable_time_from` = #{device.enableTimeFrom},
    `enable_time_to` = #{device.enableTimeTo},
    `heartbeat_time` = #{device.heartbeatTime},
    `update_time` = NOW()
    WHERE `id` = #{id}
</update>

<update id="deleteDevice">
    UPDATE `device` SET
    `is_delete` = 1
    WHERE `id` = #{id}
</update>

<select id="selectPagingDevices" resultType="run.monkey.op.xiaotong.models.Device">
    SELECT
    <include refid="deviceColumns"></include>
    FROM `device`
    WHERE `is_delete` = 0
    ORDER BY `create_time` ASC
    LIMIT #{request.paging.offset}, #{request.paging.pageSize}
</select>

<select id="selectPagingDevicesCount" resultType="Long">
    SELECT COUNT(*)
    FROM `device`
    WHERE `is_delete` = 0
</select>
```
