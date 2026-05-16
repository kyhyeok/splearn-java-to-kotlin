package kimspring.splearn.application.member.provided

import jakarta.validation.Valid
import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberInfoUpdateRequest
import kimspring.splearn.domain.member.MemberRegisterRequest

/**
 * 회원의 등록과 관련된 기능을 제공한다
 */
interface MemberRegister {
    fun register(
        @Valid registerRequest: MemberRegisterRequest,
    ): Member

    fun activate(memberId: Long): Member

    fun deactivate(memberId: Long): Member

    fun updateInfo(
        memberId: Long,
        @Valid updateRequest: MemberInfoUpdateRequest,
    ): Member
}
