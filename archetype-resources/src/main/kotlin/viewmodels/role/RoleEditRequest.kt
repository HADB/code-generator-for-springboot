package ${package_name}.viewmodels.role

import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotNull

data class RoleEditRequest(
        @ApiModelProperty(position = 0, notes = "主键", required = false, hidden = true)
        var id: Long = 0,

        @NotNull(message = "key 不能为空")
        @ApiModelProperty(position = 1, notes = "角色标识", required = true, hidden = false)
        val key: String,

        @NotNull(message = "name 不能为空")
        @ApiModelProperty(position = 2, notes = "角色名称", required = true, hidden = false)
        val name: String,

        @ApiModelProperty(position = 3, notes = "角色描述", required = false, hidden = false)
        val description: String? = null,

        @NotNull(message = "builtIn 不能为空")
        @ApiModelProperty(position = 4, notes = "是否内置", required = true, hidden = false)
        val builtIn: Int = 0
)
