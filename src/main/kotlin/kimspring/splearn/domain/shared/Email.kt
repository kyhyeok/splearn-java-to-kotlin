package kimspring.splearn.domain.shared

data class Email(
    val address: String,
) {
    companion object {
        private val EMAIL_PATTERN =
            Regex(
                "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}\$",
            )
    }

    init {
        require(EMAIL_PATTERN.matches(address)) {
            "이메일 형식이 바르지 않습니다: $address"
        }
    }
}
