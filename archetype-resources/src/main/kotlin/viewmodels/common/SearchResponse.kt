package ${package_name}.viewmodels.common

import io.swagger.v3.oas.annotations.media.Schema

data class SearchResponse<T>(
    @field:Schema(description = "当前分页条件下的搜索结果")
    val results: List<T>,

    @field:Schema(description = "搜索结果总数")
    val count: Long
)
