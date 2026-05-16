package kimspring.splearn.application.member.required

import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.Profile
import kimspring.splearn.domain.shared.Email

interface MemberRepository {
    fun save(member: Member): Member

    fun findById(id: Long): Member?

    fun findByEmail(email: Email): Member?

    fun findByProfile(profile: Profile): Member?
}
