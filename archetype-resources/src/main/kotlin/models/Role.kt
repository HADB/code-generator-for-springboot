package ${package_name}.models

import io.swagger.annotations.ApiModelProperty
import ${package_name}.annotations.NoArg
import java.math.BigDecimal
import java.util.*

@NoArg
data class Role(
        @ApiModelProperty(position = 0, notes = "主键")
        val id: Long = 0,

        @ApiModelProperty(position = 1, notes = "角色标识")
        val key: String,

        @ApiModelProperty(position = 2, notes = "角色名称")
        val name: String,

        @ApiModelProperty(position = 3, notes = "角色描述")
        val description: String? = null,

        @ApiModelProperty(position = 4, notes = "是否内置")
        val builtIn: Int = 0,

        @ApiModelProperty(position = 5, notes = "创建时间")
        val createTime: Date? = null,

        @ApiModelProperty(position = 6, notes = "更新时间")
        val updateTime: Date? = null
)
