package kimspring.splearn.application.member

import kimspring.splearn.application.member.provided.MemberFinder
import kimspring.splearn.application.member.provided.MemberRegister
import kimspring.splearn.application.member.required.EmailSender
import kimspring.splearn.application.member.required.MemberRepository
import kimspring.splearn.domain.member.DuplicateEmailException
import kimspring.splearn.domain.member.DuplicateProfileException
import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberInfoUpdateRequest
import kimspring.splearn.domain.member.MemberRegisterRequest
import kimspring.splearn.domain.member.PasswordEncoder
import kimspring.splearn.domain.member.Profile
import kimspring.splearn.domain.shared.Email
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Transactional
@Validated
class MemberModifyService(
    private val memberFinder: MemberFinder,
    private val memberRepository: MemberRepository,
    private val emailSender: EmailSender,
    private val passwordEncoder: PasswordEncoder,
) : MemberRegister {
    override fun register(registerRequest: MemberRegisterRequest): Member {
        checkDuplicateEmail(registerRequest)
        val member = Member.register(registerRequest, passwordEncoder)
        val saved = memberRepository.save(member)
        sendWelcomeEmail(saved)
        return saved
    }

    override fun activate(memberId: Long): Member {
        val member = memberFinder.find(memberId)
        return memberRepository.save(member.activate())
    }

    override fun deactivate(memberId: Long): Member {
        val member = memberFinder.find(memberId)
        return memberRepository.save(member.deactivate())
    }

    override fun updateInfo(
        memberId: Long,
        updateRequest: MemberInfoUpdateRequest,
    ): Member {
        val member = memberFinder.find(memberId)
        checkDuplicateProfile(member, updateRequest.profileAddress)
        return memberRepository.save(member.updateInfo(updateRequest))
    }

    private fun checkDuplicateProfile(
        member: Member,
        profileAddress: String,
    ) {
        if (profileAddress.isEmpty()) return
        val currentProfile = member.detail.profile
        if (currentProfile != null && currentProfile.address == profileAddress) return
        if (memberRepository.findByProfile(Profile(profileAddress)) != null) {
            throw DuplicateProfileException("이미 존재하는 프로필 주소입니다: $profileAddress")
        }
    }

    private fun sendWelcomeEmail(member: Member) {
        emailSender.send(member.email, "등록을 완료해주세요.", "아래 링크를 클릭해서 등록을 완료해주세요.")
    }

    private fun checkDuplicateEmail(registerRequest: MemberRegisterRequest) {
        if (memberRepository.findByEmail(Email(registerRequest.email)) != null) {
            throw DuplicateEmailException("이미 사용중인 이메일입니다: ${registerRequest.email}")
        }
    }
}
