package ${package_name}.models

import io.swagger.annotations.ApiModelProperty
import ${package_name}.annotations.NoArg
import java.math.BigDecimal
import java.util.*

@NoArg
data class Permission(
        @ApiModelProperty(position = 0, notes = "主键")
        val id: Long = 0,

        @ApiModelProperty(position = 1, notes = "权限标识")
        val key: String,

        @ApiModelProperty(position = 2, notes = "权限名称")
        val name: String,

        @ApiModelProperty(position = 3, notes = "权限描述")
        val description: String? = null,

        @ApiModelProperty(position = 4, notes = "API 路径")
        val apiPath: String? = null,

        @ApiModelProperty(position = 5, notes = "API 方法")
        val apiMethod: String? = null,

        @ApiModelProperty(position = 6, notes = "创建时间")
        val createTime: Date? = null,

        @ApiModelProperty(position = 7, notes = "更新时间")
        val updateTime: Date? = null
)
