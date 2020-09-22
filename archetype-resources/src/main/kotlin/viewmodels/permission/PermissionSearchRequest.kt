package ${package_name}.viewmodels.permission

import io.swagger.annotations.ApiModelProperty
import ${package_name}.models.Paging
import java.math.BigDecimal
import java.util.*

data class PermissionSearchRequest(
        @ApiModelProperty(position = 0, notes = "权限标识")
        val key: String? = null,

        @ApiModelProperty(position = 1, notes = "权限名称")
        val name: String? = null,

        @ApiModelProperty(position = 2, notes = "权限描述")
        val description: String? = null,

        @ApiModelProperty(position = 3, notes = "API 路径")
        val apiPath: String? = null,

        @ApiModelProperty(position = 4, notes = "API 方法")
        val apiMethod: String? = null,

        @ApiModelProperty(position = 5, notes = "创建时间 From")
        val createTimeFrom: Date? = null,

        @ApiModelProperty(position = 6, notes = "创建时间 To")
        val createTimeTo: Date? = null,

        @ApiModelProperty(position = 7, notes = "更新时间 From")
        val updateTimeFrom: Date? = null,

        @ApiModelProperty(position = 8, notes = "更新时间 To")
        val updateTimeTo: Date? = null,

        @ApiModelProperty(position = 97, notes = "排序字段")
        val sortBy: String? = null,

        @ApiModelProperty(position = 98, notes = "排序顺序")
        val sortOrder: String? = null,

        @ApiModelProperty(position = 99, notes = "分页(默认第1页，每页显示10条)")
        val paging: Paging = Paging(1,10)
)
