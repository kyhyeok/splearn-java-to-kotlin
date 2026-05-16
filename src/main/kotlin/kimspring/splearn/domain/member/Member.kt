package kimspring.splearn.domain.member

import kimspring.splearn.domain.shared.Email
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Table

@Table("member")
data class Member(
    @Id val id: Long? = null,
    @Embedded.Empty(prefix = "email_") val email: Email,
    val nickname: String,
    val passwordHash: String,
    val status: MemberStatus,
    val detail: MemberDetail,
) {
    fun activate(): Member {
        check(status == MemberStatus.PENDING) { "PENDING 상태가 아닙니다." }
        return copy(status = MemberStatus.ACTIVE, detail = detail.recordActivation())
    }

    fun deactivate(): Member {
        check(status == MemberStatus.ACTIVE) { "ACTIVE 상태가 아닙니다." }
        return copy(status = MemberStatus.DEACTIVATED, detail = detail.recordDeactivation())
    }

    fun updateInfo(req: MemberInfoUpdateRequest): Member {
        check(status == MemberStatus.ACTIVE) { "등록 완료 상태가 아니면 정보를 수정할 수 없습니다." }
        return copy(nickname = req.nickname, detail = detail.updateInfo(req))
    }

    fun changePassword(
        newPassword: String,
        encoder: PasswordEncoder,
    ): Member = copy(passwordHash = encoder.encode(newPassword))

    fun verifyPassword(
        password: String,
        encoder: PasswordEncoder,
    ): Boolean = encoder.matches(password, passwordHash)

    fun isActive(): Boolean = status == MemberStatus.ACTIVE

    companion object {
        fun register(
            req: MemberRegisterRequest,
            encoder: PasswordEncoder,
        ): Member =
            Member(
                email = Email(req.email),
                nickname = req.nickname,
                passwordHash = encoder.encode(req.password),
                status = MemberStatus.PENDING,
                detail = MemberDetail.create(),
            )
    }
}
