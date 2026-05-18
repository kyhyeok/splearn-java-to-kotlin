package kimspring.splearn.domain.member

import kimspring.splearn.domain.shared.Email
import java.time.LocalDateTime

data class Member(
    val id: Long? = null,
    val email: Email,
    val nickname: String,
    val passwordHash: String,
    val status: MemberStatus,
    val detail: MemberDetail,
) {
    fun activate(now: LocalDateTime): Member {
        if (status != MemberStatus.PENDING) throw InvalidMemberStateException("PENDING 상태가 아닙니다.")
        return copy(status = MemberStatus.ACTIVE, detail = detail.recordActivation(now))
    }

    fun deactivate(now: LocalDateTime): Member {
        if (status != MemberStatus.ACTIVE) throw InvalidMemberStateException("ACTIVE 상태가 아닙니다.")
        return copy(status = MemberStatus.DEACTIVATED, detail = detail.recordDeactivation(now))
    }

    fun updateInfo(
        nickname: String,
        profileAddress: String,
        introduction: String,
    ): Member {
        if (status != MemberStatus.ACTIVE) throw InvalidMemberStateException("등록 완료 상태가 아니면 정보를 수정할 수 없습니다.")
        return copy(nickname = nickname, detail = detail.updateInfo(profileAddress, introduction))
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
            email: Email,
            nickname: String,
            password: String,
            encoder: PasswordEncoder,
            now: LocalDateTime,
        ): Member =
            Member(
                email = email,
                nickname = nickname,
                passwordHash = encoder.encode(password),
                status = MemberStatus.PENDING,
                detail = MemberDetail.create(now),
            )
    }
}
