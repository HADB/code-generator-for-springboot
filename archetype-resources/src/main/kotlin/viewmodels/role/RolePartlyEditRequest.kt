package ${package_name}.viewmodels.role

import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotNull

data class RolePartlyEditRequest(
        @ApiModelProperty(position = 0, notes = "主键")
        val id: Long,

        @ApiModelProperty(position = 1, notes = "角色标识")
        val key: String? = null,

        @ApiModelProperty(position = 2, notes = "角色名称")
        val name: String? = null,

        @ApiModelProperty(position = 3, notes = "角色描述")
        val description: String? = null,

        @ApiModelProperty(position = 4, notes = "是否内置")
        val builtIn: Int? = null
)
