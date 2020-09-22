package ${package_name}.models

import io.swagger.annotations.ApiModelProperty
import ${package_name}.annotations.NoArg
import java.math.BigDecimal
import java.util.*

@NoArg
data class UserRole(
        @ApiModelProperty(position = 0, notes = "主键")
        val id: Long = 0,

        @ApiModelProperty(position = 1, notes = "用户ID")
        val userId: Long,

        @ApiModelProperty(position = 2, notes = "角色ID")
        val roleId: Long,

        @ApiModelProperty(position = 3, notes = "创建时间")
        val createTime: Date? = null,

        @ApiModelProperty(position = 4, notes = "更新时间")
        val updateTime: Date? = null
)
