package kimspring.splearn.domain.member

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("member_detail")
data class MemberDetail(
    @Id val id: Long? = null,
    @Embedded.Nullable(prefix = "profile_") val profile: Profile? = null,
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

    fun updateInfo(req: MemberInfoUpdateRequest): MemberDetail =
        copy(profile = Profile(req.profileAddress), introduction = req.introduction)

    companion object {
        fun create(): MemberDetail = MemberDetail(registeredAt = LocalDateTime.now())
    }
}
