package kimspring.splearn.application.member

import kimspring.splearn.application.member.command.LoginMemberCommand
import kimspring.splearn.application.member.port.MemberRepository
import kimspring.splearn.application.member.usecase.MemberAuthenticator
import kimspring.splearn.domain.member.LoginFailedException
import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.PasswordEncoder
import kimspring.splearn.domain.shared.Email
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Transactional(readOnly = true)
@Validated
class MemberAuthenticationService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
) : MemberAuthenticator {
    override fun login(command: LoginMemberCommand): Member {
        val member = memberRepository.findByEmail(Email(command.email))

        // 세 조건을 한 번에 검사한다 — 어느 단계에서 실패했는지 호출자가 구분할 수 없어야 한다
        if (member == null ||
            !member.isActive() ||
            !member.verifyPassword(command.password, passwordEncoder)
        ) {
            throw LoginFailedException()
        }

        return member
    }
}
