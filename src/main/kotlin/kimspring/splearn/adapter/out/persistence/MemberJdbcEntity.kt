package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberStatus
import kimspring.splearn.domain.shared.Email
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("member")
data class MemberJdbcEntity(
    @Id val id: Long? = null,
    val emailAddress: String,
    val nickname: String,
    val passwordHash: String,
    val status: MemberStatus,
    val detail: MemberDetailJdbcEntity,
) {
    fun toDomain(): Member =
        Member(
            id = id,
            email = Email(emailAddress),
            nickname = nickname,
            passwordHash = passwordHash,
            status = status,
            detail = detail.toDomain(),
        )

    companion object {
        fun from(member: Member): MemberJdbcEntity =
            MemberJdbcEntity(
                id = member.id,
                emailAddress = member.email.address,
                nickname = member.nickname,
                passwordHash = member.passwordHash,
                status = member.status,
                detail = MemberDetailJdbcEntity.from(member.detail),
            )
    }
}
