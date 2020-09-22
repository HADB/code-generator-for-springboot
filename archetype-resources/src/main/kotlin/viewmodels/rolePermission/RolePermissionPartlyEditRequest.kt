package ${package_name}.viewmodels.rolePermission

import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotNull

data class RolePermissionPartlyEditRequest(
        @ApiModelProperty(position = 0, notes = "主键")
        val id: Long,

        @ApiModelProperty(position = 1, notes = "角色ID")
        val roleId: Long? = null,

        @ApiModelProperty(position = 2, notes = "权限ID")
        val permissionId: Long? = null
)
