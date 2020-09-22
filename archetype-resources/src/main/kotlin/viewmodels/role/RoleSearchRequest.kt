package ${package_name}.viewmodels.role

import io.swagger.annotations.ApiModelProperty
import ${package_name}.models.Paging
import java.math.BigDecimal
import java.util.*

data class RoleSearchRequest(
        @ApiModelProperty(position = 0, notes = "角色标识")
        val key: String? = null,

        @ApiModelProperty(position = 1, notes = "角色名称")
        val name: String? = null,

        @ApiModelProperty(position = 2, notes = "角色描述")
        val description: String? = null,

        @ApiModelProperty(position = 3, notes = "是否内置")
        val builtIn: Int? = null,

        @ApiModelProperty(position = 4, notes = "创建时间 From")
        val createTimeFrom: Date? = null,

        @ApiModelProperty(position = 5, notes = "创建时间 To")
        val createTimeTo: Date? = null,

        @ApiModelProperty(position = 6, notes = "更新时间 From")
        val updateTimeFrom: Date? = null,

        @ApiModelProperty(position = 7, notes = "更新时间 To")
        val updateTimeTo: Date? = null,

        @ApiModelProperty(position = 97, notes = "排序字段")
        val sortBy: String? = null,

        @ApiModelProperty(position = 98, notes = "排序顺序")
        val sortOrder: String? = null,

        @ApiModelProperty(position = 99, notes = "分页(默认第1页，每页显示10条)")
        val paging: Paging = Paging(1,10)
)
