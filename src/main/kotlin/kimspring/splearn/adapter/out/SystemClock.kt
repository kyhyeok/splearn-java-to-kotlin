package kimspring.splearn.adapter.out

import kimspring.splearn.domain.shared.Clock
import org.springframework.context.annotation.Fallback
import org.springframework.stereotype.Component
import java.time.LocalDateTime

// 테스트 구성이 고정 Clock 을 등록하면 그쪽이 우선한다. 파라미터명-빈이름 일치에 의존하지 않도록 명시한다
@Component
@Fallback
class SystemClock : Clock {
    override fun now(): LocalDateTime = LocalDateTime.now()
}
