package kimspring.splearn.domain.shared

import java.time.LocalDateTime

fun interface Clock {
    fun now(): LocalDateTime
}
