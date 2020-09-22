package ${package_name}.viewmodels.permission

import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotNull

data class PermissionPartlyEditRequest(
        @ApiModelProperty(position = 0, notes = "主键")
        val id: Long,

        @ApiModelProperty(position = 1, notes = "权限标识")
        val key: String? = null,

        @ApiModelProperty(position = 2, notes = "权限名称")
        val name: String? = null,

        @ApiModelProperty(position = 3, notes = "权限描述")
        val description: String? = null,

        @ApiModelProperty(position = 4, notes = "API 路径")
        val apiPath: String? = null,

        @ApiModelProperty(position = 5, notes = "API 方法")
        val apiMethod: String? = null
)
