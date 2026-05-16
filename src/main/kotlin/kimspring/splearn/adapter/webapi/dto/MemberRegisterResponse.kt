package kimspring.splearn.adapter.webapi.dto

import kimspring.splearn.domain.member.Member

data class MemberRegisterResponse(
    val memberId: Long?,
    val email: String,
) {
    companion object {
        fun of(member: Member) =
            MemberRegisterResponse(
                memberId = member.id,
                email = member.email.address,
            )
    }
}
