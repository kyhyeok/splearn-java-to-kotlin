package kimspring.splearn.domain.shared

data class Email(
    val address: String,
) {
    companion object {
        // Command 의 Bean Validation 이 같은 규칙을 참조한다 — 형식 정책의 단일 출처
        const val EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}\$"
        private val EMAIL_PATTERN = Regex(EMAIL_REGEX)
    }

    init {
        // 주소값은 개인정보라 예외 메시지(로그)에 싣지 않는다
        require(EMAIL_PATTERN.matches(address)) { "이메일 형식이 바르지 않습니다." }
    }
}
