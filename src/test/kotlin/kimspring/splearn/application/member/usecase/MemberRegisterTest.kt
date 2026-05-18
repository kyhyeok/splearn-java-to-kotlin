package kimspring.splearn.application.member.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.validation.ConstraintViolationException
import kimspring.splearn.SplearnTestConfiguration
import kimspring.splearn.application.member.command.RegisterMemberCommand
import kimspring.splearn.application.member.command.UpdateMemberInfoCommand
import kimspring.splearn.application.member.usecase.MemberLifecycle
import kimspring.splearn.application.member.usecase.MemberModifier
import kimspring.splearn.domain.member.DuplicateEmailException
import kimspring.splearn.domain.member.DuplicateProfileException
import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberFixture
import kimspring.splearn.domain.member.MemberStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Import(SplearnTestConfiguration::class)
class MemberRegisterTest : FunSpec() {
    @Autowired
    private lateinit var memberRegister: MemberRegister

    @Autowired
    private lateinit var memberLifecycle: MemberLifecycle

    @Autowired
    private lateinit var memberModifier: MemberModifier

    init {
        extension(SpringExtension())

        test("register") {
            val member = memberRegister.register(MemberFixture.createRegisterMemberCommand())

            member.shouldNotBeNull()
            member.status shouldBe MemberStatus.PENDING
        }

        test("duplicateEmailFail") {
            memberRegister.register(MemberFixture.createRegisterMemberCommand())

            shouldThrow<DuplicateEmailException> {
                memberRegister.register(MemberFixture.createRegisterMemberCommand())
            }
        }

        test("activate") {
            val member = registerMember()

            val activated = memberLifecycle.activate(member.id!!)

            activated.status shouldBe MemberStatus.ACTIVE
            activated.detail.activatedAt.shouldNotBeNull()
        }

        test("deactivate") {
            val member = registerMember()

            val activated = memberLifecycle.activate(member.id!!)
            val deactivated = memberLifecycle.deactivate(activated.id!!)

            deactivated.status shouldBe MemberStatus.DEACTIVATED
            deactivated.detail.deactivatedAt.shouldNotBeNull()
        }

        test("updateInfo") {
            val member = registerMember()
            val activated = memberLifecycle.activate(member.id!!)

            val command = UpdateMemberInfoCommand("Hyeok", "kim001", "자기소개")
            val updated = memberModifier.updateInfo(activated.id!!, command)

            updated.detail.profile!!.address shouldBe command.profileAddress
        }

        test("updateInfoFail") {
            val memberId = requireNotNull(registerMember().id)
            memberLifecycle.activate(memberId)
            memberModifier.updateInfo(memberId, UpdateMemberInfoCommand("Hyeok", "kim001", "자기소개"))

            val member2Id = requireNotNull(registerMember("kiim@splearn.app").id)
            memberLifecycle.activate(member2Id)

            // member2는 기존의 member와 같은 프로필 주소를 사용할 수 없다
            shouldThrow<DuplicateProfileException> {
                memberModifier.updateInfo(member2Id, UpdateMemberInfoCommand("Kimmy", "kim001", "자기소개임"))
            }

            // 다른 프로필 주소로는 변경 가능
            memberModifier.updateInfo(member2Id, UpdateMemberInfoCommand("Kimmy", "kim002", "자기소개임"))

            // 기존 프로필 주소를 바꾸는 것도 가능
            memberModifier.updateInfo(memberId, UpdateMemberInfoCommand("Kimmy", "kim001", "자기소개임"))

            // 프로필 주소를 제거하는 것도 가능
            memberModifier.updateInfo(memberId, UpdateMemberInfoCommand("Kimmy", "", "자기소개임"))

            // 프로필 주소 중복은 허용하지 않음
            shouldThrow<DuplicateProfileException> {
                memberModifier.updateInfo(memberId, UpdateMemberInfoCommand("Kimmy", "kim002", "자기소개임"))
            }
        }

        test("memberRegisterCommandFail") {
            checkValidation(RegisterMemberCommand("kim@splearn.app", "Kim", "secret1234"))
            checkValidation(RegisterMemberCommand("kim@splearn.app", "KimLongNameSplearnTestCode", "secret1234"))
            checkValidation(RegisterMemberCommand("kimsplearn.app", "KimLongName", "secret1234"))
            checkValidation(RegisterMemberCommand("kim#splearn.app", "KimLongName", "secret"))
        }
    }

    private fun checkValidation(invalid: RegisterMemberCommand) {
        shouldThrow<ConstraintViolationException> { memberRegister.register(invalid) }
    }

    private fun registerMember(): Member = memberRegister.register(MemberFixture.createRegisterMemberCommand())

    private fun registerMember(email: String): Member =
        memberRegister.register(MemberFixture.createRegisterMemberCommand(email))
}
