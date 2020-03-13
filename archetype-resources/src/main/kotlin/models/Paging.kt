package ${package}.models

import ${package}.annotations.NoArg

@NoArg
data class Paging(
        private val pageNumber: Long,
        private val pageSize: Int
) {
    val offset: Long get() = (this.pageNumber - 1) * this.pageSize
}