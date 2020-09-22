package ${package_name}.viewmodels.userRole

import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotNull

data class UserRoleEditRequest(
        @ApiModelProperty(position = 0, notes = "主键", required = false, hidden = true)
        var id: Long = 0,

        @NotNull(message = "userId 不能为空")
        @ApiModelProperty(position = 1, notes = "用户ID", required = true, hidden = false)
        val userId: Long,

        @NotNull(message = "roleId 不能为空")
        @ApiModelProperty(position = 2, notes = "角色ID", required = true, hidden = false)
        val roleId: Long
)
