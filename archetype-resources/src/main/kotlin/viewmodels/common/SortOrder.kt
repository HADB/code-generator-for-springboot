package ${package_name}.viewmodels.common

import io.swagger.v3.oas.annotations.media.Schema

data class SortOrder(
    @Schema(description = "排序字段")
    val field: String,

    @Schema(description = "排序方向")
    val direction: String
)
