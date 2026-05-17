package kimspring.splearn.domain.member

import java.time.LocalDateTime

data class MemberDetail(
    val id: Long? = null,
    val profile: Profile? = null,
    val introduction: String? = null,
    val registeredAt: LocalDateTime,
    val activatedAt: LocalDateTime? = null,
    val deactivatedAt: LocalDateTime? = null,
) {
    fun recordActivation(): MemberDetail {
        check(activatedAt == null) { "이미 activatedAt은 설정되었습니다" }
        return copy(activatedAt = LocalDateTime.now())
    }

    fun recordDeactivation(): MemberDetail {
        check(deactivatedAt == null) { "이미 deactivatedAt은 설정되었습니다" }
        return copy(deactivatedAt = LocalDateTime.now())
    }

    fun updateInfo(profileAddress: String, introduction: String): MemberDetail =
        copy(profile = Profile(profileAddress), introduction = introduction)

    companion object {
        fun create(): MemberDetail = MemberDetail(registeredAt = LocalDateTime.now())
    }
}
