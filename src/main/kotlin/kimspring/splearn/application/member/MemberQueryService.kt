package kimspring.splearn.application.member

import kimspring.splearn.application.member.provided.MemberFinder
import kimspring.splearn.application.member.required.MemberRepository
import kimspring.splearn.domain.member.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Transactional(readOnly = true)
@Validated
class MemberQueryService(
    private val memberRepository: MemberRepository,
) : MemberFinder {
    override fun find(memberId: Long): Member =
        memberRepository.findById(memberId)
            ?: throw IllegalArgumentException("회원을 찾을 수 없습니다. id: $memberId")
}
