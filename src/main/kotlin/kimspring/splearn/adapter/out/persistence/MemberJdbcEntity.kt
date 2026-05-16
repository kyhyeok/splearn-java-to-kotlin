package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberStatus
import kimspring.splearn.domain.shared.Email
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Table

@Table("member")
data class MemberJdbcEntity(
    @Id val id: Long? = null,
    @Embedded.Empty(prefix = "email_") val email: Email,
    val nickname: String,
    val passwordHash: String,
    val status: MemberStatus,
    val detail: MemberDetailJdbcEntity,
) {
    fun toDomain(): Member =
        Member(
            id = id,
            email = email,
            nickname = nickname,
            passwordHash = passwordHash,
            status = status,
            detail = detail.toDomain(),
        )

    companion object {
        fun from(member: Member): MemberJdbcEntity =
            MemberJdbcEntity(
                id = member.id,
                email = member.email,
                nickname = member.nickname,
                passwordHash = member.passwordHash,
                status = member.status,
                detail = MemberDetailJdbcEntity.from(member.detail),
            )
    }
}
