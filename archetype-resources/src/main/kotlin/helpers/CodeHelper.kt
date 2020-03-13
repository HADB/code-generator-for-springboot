package ${package}.helpers

import org.springframework.stereotype.Component

@Component
class CodeHelper {
    fun getCode(length: Int): String {
        val code = Math.round(Math.random() * Math.pow(10.0, length.toDouble()))
        val format = String.format("%%0%dd", length)
        return String.format(format, code)
    }
}
