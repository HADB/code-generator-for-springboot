package ${package_name}.viewmodels.rolePermission

import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotNull

data class RolePermissionEditRequest(
        @ApiModelProperty(position = 0, notes = "主键", required = false, hidden = true)
        var id: Long = 0,

        @NotNull(message = "roleId 不能为空")
        @ApiModelProperty(position = 1, notes = "角色ID", required = true, hidden = false)
        val roleId: Long,

        @NotNull(message = "permissionId 不能为空")
        @ApiModelProperty(position = 2, notes = "权限ID", required = true, hidden = false)
        val permissionId: Long
)
