package ${package_name}.viewmodels.common

import io.swagger.annotations.ApiModelProperty

data class SearchResponse<T>(
    @ApiModelProperty(position = 1, notes = "当前分页条件下的搜索结果")
    val results: List<T>,

    @ApiModelProperty(position = 2, notes = "搜索结果总数")
    val count: Long
)

