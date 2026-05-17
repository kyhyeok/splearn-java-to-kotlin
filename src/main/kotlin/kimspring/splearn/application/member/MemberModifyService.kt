package kimspring.splearn.application.member

import io.github.oshai.kotlinlogging.KotlinLogging
import kimspring.splearn.application.member.command.RegisterMemberCommand
import kimspring.splearn.application.member.command.UpdateMemberInfoCommand
import kimspring.splearn.application.member.port.EmailSender
import kimspring.splearn.application.member.port.MemberRepository
import kimspring.splearn.application.member.usecase.MemberFinder
import kimspring.splearn.application.member.usecase.MemberRegister
import kimspring.splearn.domain.member.DuplicateEmailException
import kimspring.splearn.domain.member.DuplicateProfileException
import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.PasswordEncoder
import kimspring.splearn.domain.member.Profile
import kimspring.splearn.domain.shared.Email
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

private val log = KotlinLogging.logger {}

@Service
@Transactional
@Validated
class MemberModifyService(
    private val memberFinder: MemberFinder,
    private val memberRepository: MemberRepository,
    private val emailSender: EmailSender,
    private val passwordEncoder: PasswordEncoder,
) : MemberRegister {
    override fun register(command: RegisterMemberCommand): Member {
        checkDuplicateEmail(command)
        val member = Member.register(Email(command.email), command.nickname, command.password, passwordEncoder)
        val saved = memberRepository.save(member)
        sendWelcomeEmail(saved)
        log.info { "회원 가입 완료: memberId=${saved.id}" }
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
        command: UpdateMemberInfoCommand,
    ): Member {
        val member = memberFinder.find(memberId)
        checkDuplicateProfile(member, command.profileAddress)
        return memberRepository.save(member.updateInfo(command.nickname, command.profileAddress, command.introduction))
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

    private fun checkDuplicateEmail(command: RegisterMemberCommand) {
        if (memberRepository.findByEmail(Email(command.email)) != null) {
            throw DuplicateEmailException("이미 사용중인 이메일입니다: ${command.email}")
        }
    }
}
