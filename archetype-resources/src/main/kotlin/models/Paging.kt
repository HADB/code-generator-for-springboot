package ${package_name}.models

import io.swagger.v3.oas.annotations.media.Schema
import ${package_name}.annotations.NoArg

@NoArg
data class Paging(
    @Schema(description = "分页序号", example = "1")
    val pageNumber: Long = 1,

    @Schema(description = "分页大小", example = "10")
    val pageSize: Int = 10
) {
    @get:Schema(hidden = true)
    val offset: Long get() = (this.pageNumber - 1) * this.pageSize
}
