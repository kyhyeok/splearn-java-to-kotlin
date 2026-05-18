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
    fun recordActivation(now: LocalDateTime): MemberDetail {
        check(activatedAt == null) { "이미 activatedAt은 설정되었습니다" }
        return copy(activatedAt = now)
    }

    fun recordDeactivation(now: LocalDateTime): MemberDetail {
        check(deactivatedAt == null) { "이미 deactivatedAt은 설정되었습니다" }
        return copy(deactivatedAt = now)
    }

    fun updateInfo(
        profileAddress: String,
        introduction: String,
    ): MemberDetail = copy(profile = Profile(profileAddress), introduction = introduction)

    companion object {
        fun create(now: LocalDateTime): MemberDetail = MemberDetail(registeredAt = now)
    }
}
