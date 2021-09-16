package ${package_name}.viewmodels.common

import io.swagger.annotations.ApiModelProperty

data class SortOrder(
    @ApiModelProperty(position = 1, notes = "排序字段")
    val field: String,

    @ApiModelProperty(position = 2, notes = "排序方向")
    val direction: String
)
