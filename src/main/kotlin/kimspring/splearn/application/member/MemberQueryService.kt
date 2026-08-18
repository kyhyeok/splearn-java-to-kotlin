package kimspring.splearn.application.member

import kimspring.splearn.application.member.port.MemberRepository
import kimspring.splearn.application.member.usecase.MemberFinder
import kimspring.splearn.domain.member.Member
import kimspring.splearn.support.stereotype.QueryApplicationService

@QueryApplicationService
class MemberQueryService(
    private val memberRepository: MemberRepository,
) : MemberFinder {
    override fun find(memberId: Long): Member = memberRepository.getById(memberId)
}
