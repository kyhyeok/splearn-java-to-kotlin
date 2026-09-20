package kimspring.splearn.domain.member

data class Profile(
    val address: String,
) {
    companion object {
        // Command 의 Bean Validation 이 같은 규칙을 참조한다 — 형식·길이 정책의 단일 출처
        const val ADDRESS_REGEX = "[a-z0-9]+"
        const val MAX_ADDRESS_LENGTH = 15
        private val PROFILE_ADDRESS_PATTERN = Regex(ADDRESS_REGEX)
    }

    init {
        require(PROFILE_ADDRESS_PATTERN.matches(address)) {
            "프로필 주소 형식이 바르지 않습니다: $address"
        }
        require(address.length <= MAX_ADDRESS_LENGTH) { "프로필 주소는 최대 ${MAX_ADDRESS_LENGTH}자리를 넘을 수 없습니다" }
    }

    fun url(): String = "@$address"
}
