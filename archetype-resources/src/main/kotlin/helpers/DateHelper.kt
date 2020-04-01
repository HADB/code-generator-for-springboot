package ${package_name}.helpers

import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Component
class DateHelper {
    fun duration(startDate: Date?, endDate: Date?): Duration {
        val date1 = startDate ?: Date(0)
        val date2 = endDate ?: Date(0)
        val localDateTime1 = getLocalDateTime(date1)
        val localDateTime2 = getLocalDateTime(date2)
        return Duration.between(localDateTime1, localDateTime2)
    }

    private fun getLocalDateTime(date: Date): LocalDateTime {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
    }
}
