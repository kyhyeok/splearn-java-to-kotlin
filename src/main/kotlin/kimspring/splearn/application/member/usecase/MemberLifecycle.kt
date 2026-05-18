package kimspring.splearn.application.member.usecase

import kimspring.splearn.domain.member.Member

interface MemberLifecycle {
    fun activate(memberId: Long): Member

    fun deactivate(memberId: Long): Member
}
