package kimspring.splearn.domain.shared

@ConsistentCopyVisibility
data class Email private constructor(
    val address: String,
) {
    companion object {
        // Command 의 Bean Validation 이 같은 규칙을 참조한다 — 형식 정책의 단일 출처
        const val EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}\$"
        private val EMAIL_PATTERN = Regex(EMAIL_REGEX)

        // 이메일은 대소문자를 구분하지 않는다는 도메인 정책. DB 콜레이션에 맡기지 않고 소문자로 정규화해 저장·비교한다
        operator fun invoke(address: String): Email = Email(address.lowercase())
    }

    init {
        // 주소값은 개인정보라 예외 메시지(로그)에 싣지 않는다
        require(EMAIL_PATTERN.matches(address)) { "이메일 형식이 바르지 않습니다." }
    }
}
