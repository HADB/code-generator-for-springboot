package ${package_name}.models

import io.swagger.v3.oas.annotations.media.Schema
import ${package_name}.annotations.NoArg

@NoArg
data class Paging(
    val pageNumber: Long = 1,
    val pageSize: Int = 10
) {
    @get:Schema(hidden = true)
    val offset: Long get() = (this.pageNumber - 1) * this.pageSize
}