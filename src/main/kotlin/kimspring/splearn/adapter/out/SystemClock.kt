package kimspring.splearn.adapter.out

import kimspring.splearn.domain.shared.Clock
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SystemClock : Clock {
    override fun now(): LocalDateTime = LocalDateTime.now()
}
