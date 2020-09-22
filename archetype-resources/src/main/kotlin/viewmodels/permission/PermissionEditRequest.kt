package ${package_name}.viewmodels.permission

import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotNull

data class PermissionEditRequest(
        @ApiModelProperty(position = 0, notes = "主键", required = false, hidden = true)
        var id: Long = 0,

        @NotNull(message = "key 不能为空")
        @ApiModelProperty(position = 1, notes = "权限标识", required = true, hidden = false)
        val key: String,

        @NotNull(message = "name 不能为空")
        @ApiModelProperty(position = 2, notes = "权限名称", required = true, hidden = false)
        val name: String,

        @ApiModelProperty(position = 3, notes = "权限描述", required = false, hidden = false)
        val description: String? = null,

        @ApiModelProperty(position = 4, notes = "API 路径", required = false, hidden = false)
        val apiPath: String? = null,

        @ApiModelProperty(position = 5, notes = "API 方法", required = false, hidden = false)
        val apiMethod: String? = null
)
