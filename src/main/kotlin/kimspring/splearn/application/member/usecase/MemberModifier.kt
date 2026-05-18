package kimspring.splearn.application.member.usecase

import jakarta.validation.Valid
import kimspring.splearn.application.member.command.UpdateMemberInfoCommand
import kimspring.splearn.domain.member.Member

interface MemberModifier {
    fun updateInfo(
        memberId: Long,
        @Valid command: UpdateMemberInfoCommand,
    ): Member
}
