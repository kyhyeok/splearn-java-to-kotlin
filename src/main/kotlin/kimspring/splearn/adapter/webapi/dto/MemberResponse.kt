package kimspring.splearn.adapter.webapi.dto

import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberStatus

data class MemberResponse(
    val memberId: Long?,
    val email: String,
    val nickname: String,
    val status: MemberStatus,
) {
    companion object {
        fun of(member: Member) =
            MemberResponse(
                memberId = member.id,
                email = member.email.address,
                nickname = member.nickname,
                status = member.status,
            )
    }
}
