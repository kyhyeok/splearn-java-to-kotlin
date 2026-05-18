package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.domain.member.MemberDetail
import kimspring.splearn.domain.member.Profile
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("member_detail")
data class MemberDetailJdbcEntity(
    @Id val id: Long? = null,
    val profileAddress: String? = null,
    val introduction: String? = null,
    val registeredAt: LocalDateTime,
    val activatedAt: LocalDateTime? = null,
    val deactivatedAt: LocalDateTime? = null,
) {
    fun toDomain(): MemberDetail =
        MemberDetail(
            id = id,
            profile = profileAddress?.let { Profile(it) },
            introduction = introduction,
            registeredAt = registeredAt,
            activatedAt = activatedAt,
            deactivatedAt = deactivatedAt,
        )

    companion object {
        fun from(detail: MemberDetail): MemberDetailJdbcEntity =
            MemberDetailJdbcEntity(
                id = detail.id,
                profileAddress = detail.profile?.address,
                introduction = detail.introduction,
                registeredAt = detail.registeredAt,
                activatedAt = detail.activatedAt,
                deactivatedAt = detail.deactivatedAt,
            )
    }
}
