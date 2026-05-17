package kimspring.splearn.application.member

import kimspring.splearn.application.member.port.MemberRepository
import kimspring.splearn.application.member.usecase.MemberFinder
import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberNotFoundException
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
            ?: throw MemberNotFoundException(memberId)
}
