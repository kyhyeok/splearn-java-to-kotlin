package kimspring.splearn.application.member.usecase

import jakarta.validation.Valid
import kimspring.splearn.application.member.command.RegisterMemberCommand
import kimspring.splearn.application.member.command.UpdateMemberInfoCommand
import kimspring.splearn.domain.member.Member

/**
 * 회원의 등록과 관련된 기능을 제공한다
 */
interface MemberRegister {
    fun register(
        @Valid command: RegisterMemberCommand,
    ): Member

    fun activate(memberId: Long): Member

    fun deactivate(memberId: Long): Member

    fun updateInfo(
        memberId: Long,
        @Valid command: UpdateMemberInfoCommand,
    ): Member
}
