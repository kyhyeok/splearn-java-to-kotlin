package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.application.member.port.MemberRepository
import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.Profile
import kimspring.splearn.domain.shared.Email
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class MemberRepositoryAdapter(
    private val springMemberRepository: SpringMemberRepository,
) : MemberRepository {
    override fun save(member: Member): Member = springMemberRepository.save(MemberJdbcEntity.from(member)).toDomain()

    override fun findById(id: Long): Member? = springMemberRepository.findByIdOrNull(id)?.toDomain()

    override fun findByEmail(email: Email): Member? =
        springMemberRepository.findByEmailAddress(email.address)?.toDomain()

    override fun findByProfile(profile: Profile): Member? =
        springMemberRepository.findByProfileAddress(profile.address)?.toDomain()
}
