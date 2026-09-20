package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberStatus
import kimspring.splearn.domain.shared.Email
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table("member")
data class MemberJdbcEntity(
    @Id val id: Long? = null,
    @Version val version: Long? = null,
    val emailAddress: String,
    val nickname: String,
    val passwordHash: String,
    val status: MemberStatus,
    val activationToken: String?,
    // 1:1 detail 은 루트 재저장 시 삭제 후 재삽입된다(UPDATE 1 + DELETE 1 + INSERT 1). 현재 규모에서 수용한다.
    // 쓰기가 잦아지면 @Embedded 로 부모 테이블에 합쳐 UPDATE 1문장으로 만든다(스키마 변경 필요)
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
            activationToken = activationToken,
            version = version,
        )

    companion object {
        fun from(member: Member): MemberJdbcEntity =
            MemberJdbcEntity(
                id = member.id,
                emailAddress = member.email.address,
                nickname = member.nickname,
                passwordHash = member.passwordHash,
                status = member.status,
                activationToken = member.activationToken,
                version = member.version,
                detail = MemberDetailJdbcEntity.from(member.detail),
            )
    }
}
