package kimspring.splearn.application.member.usecase

import jakarta.validation.Valid
import kimspring.splearn.application.member.command.LoginMemberCommand
import kimspring.splearn.domain.member.Member

/**
 * 회원을 인증한다
 *
 * 등록 완료(ACTIVE) 상태인 회원만 로그인할 수 있다.
 * 인증에 실패하면 원인을 구분하지 않고 `LoginFailedException`을 던진다.
 */
interface MemberAuthenticator {
    fun login(
        @Valid command: LoginMemberCommand,
    ): Member
}
