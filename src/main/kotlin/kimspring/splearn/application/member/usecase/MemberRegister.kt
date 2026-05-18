package kimspring.splearn.application.member.usecase

import jakarta.validation.Valid
import kimspring.splearn.application.member.command.RegisterMemberCommand
import kimspring.splearn.domain.member.Member

interface MemberRegister {
    fun register(
        @Valid command: RegisterMemberCommand,
    ): Member
}
