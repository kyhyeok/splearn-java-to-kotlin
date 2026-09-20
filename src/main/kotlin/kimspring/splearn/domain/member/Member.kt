package kimspring.splearn.domain.member

import kimspring.splearn.domain.shared.Email
import java.time.LocalDateTime
import java.util.UUID

data class Member(
    val id: Long? = null,
    val email: Email,
    val nickname: String,
    val passwordHash: String,
    val status: MemberStatus,
    val detail: MemberDetail,
    // 가입 메일로만 전달되는 1회용 토큰. 이메일 소유 확인이 활성화의 목적이므로 순번 id 로는 활성화할 수 없다
    val activationToken: String? = null,
) {
    fun activate(now: LocalDateTime): Member {
        if (status != MemberStatus.PENDING) throw InvalidMemberStateException("PENDING 상태가 아닙니다.")
        if (now.isAfter(detail.registeredAt.plusHours(ACTIVATION_VALID_HOURS))) {
            // 무효 토큰과 같은 문구를 쓴다 — 토큰 보유자에게 만료 여부를 구분해 알려줄 이유가 없다
            throw InvalidActivationTokenException("유효하지 않거나 만료된 활성화 토큰입니다.")
        }
        return copy(status = MemberStatus.ACTIVE, activationToken = null, detail = detail.recordActivation(now))
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

    fun ensureActive() {
        if (!isActive()) throw InvalidMemberStateException("ACTIVE 상태가 아닙니다.")
    }

    companion object {
        const val ACTIVATION_VALID_HOURS = 24L

        fun register(
            info: MemberRegisterInfo,
            encoder: PasswordEncoder,
            now: LocalDateTime,
        ): Member =
            Member(
                email = Email(info.email),
                nickname = info.nickname,
                passwordHash = encoder.encode(info.password),
                status = MemberStatus.PENDING,
                detail = MemberDetail.create(now),
                activationToken = UUID.randomUUID().toString(),
            )
    }
}
