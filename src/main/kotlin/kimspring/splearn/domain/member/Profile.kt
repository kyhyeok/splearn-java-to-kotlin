package kimspring.splearn.domain.member

data class Profile(
    val address: String = "",
) {
    companion object {
        private val PROFILE_ADDRESS_PATTERN = Regex("[a-z0-9]+")
        private const val MAX_ADDRESS_LENGTH = 15
    }

    init {
        if (address.isNotEmpty()) {
            require(PROFILE_ADDRESS_PATTERN.matches(address)) {
                "프로필 주소 형식이 바르지 않습니다: $address"
            }
            require(address.length <= MAX_ADDRESS_LENGTH) { "프로필 주소는 최대 ${MAX_ADDRESS_LENGTH}자리를 넘을 수 없습니다" }
        }
    }

    fun url(): String = "@$address"
}
