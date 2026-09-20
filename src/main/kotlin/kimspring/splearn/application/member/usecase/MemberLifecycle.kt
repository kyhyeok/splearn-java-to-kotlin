package kimspring.splearn.application.member.usecase

import kimspring.splearn.domain.member.Member

interface MemberLifecycle {
    // 가입 메일로 받은 1회용 토큰으로 등록을 완료한다. 순번 id 로는 활성화할 수 없다
    fun activate(token: String): Member

    fun deactivate(memberId: Long): Member
}
