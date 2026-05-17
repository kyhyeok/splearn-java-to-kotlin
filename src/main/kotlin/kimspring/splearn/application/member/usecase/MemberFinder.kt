package kimspring.splearn.application.member.usecase

import kimspring.splearn.domain.member.Member

/**
 * 회원을 조회한다
 */
interface MemberFinder {
    fun find(memberId: Long): Member
}
