package kimspring.splearn.application.member.provided

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.validation.ConstraintViolationException
import kimspring.splearn.SplearnTestConfiguration
import kimspring.splearn.domain.member.DuplicateEmailException
import kimspring.splearn.domain.member.DuplicateProfileException
import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberFixture
import kimspring.splearn.domain.member.MemberInfoUpdateRequest
import kimspring.splearn.domain.member.MemberRegisterRequest
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

    init {
        extension(SpringExtension())

        test("register") {
            val member = memberRegister.register(MemberFixture.createMemberRegisterRequest())

            member.shouldNotBeNull()
            member.status shouldBe MemberStatus.PENDING
        }

        test("duplicateEmailFail") {
            memberRegister.register(MemberFixture.createMemberRegisterRequest())

            shouldThrow<DuplicateEmailException> {
                memberRegister.register(MemberFixture.createMemberRegisterRequest())
            }
        }

        test("activate") {
            val member = registerMember()

            val activated = memberRegister.activate(member.id!!)

            activated.status shouldBe MemberStatus.ACTIVE
            activated.detail.activatedAt.shouldNotBeNull()
        }

        test("deactivate") {
            val member = registerMember()

            val activated = memberRegister.activate(member.id!!)
            val deactivated = memberRegister.deactivate(activated.id!!)

            deactivated.status shouldBe MemberStatus.DEACTIVATED
            deactivated.detail.deactivatedAt.shouldNotBeNull()
        }

        test("updateInfo") {
            val member = registerMember()
            val activated = memberRegister.activate(member.id!!)

            val request = MemberInfoUpdateRequest("Hyeok", "kim001", "자기소개")
            val updated = memberRegister.updateInfo(activated.id!!, request)

            updated.detail.profile!!.address shouldBe request.profileAddress
        }

        test("updateInfoFail") {
            val memberId = requireNotNull(registerMember().id)
            memberRegister.activate(memberId)
            memberRegister.updateInfo(memberId, MemberInfoUpdateRequest("Hyeok", "kim001", "자기소개"))

            val member2Id = requireNotNull(registerMember("kiim@splearn.app").id)
            memberRegister.activate(member2Id)

            // member2는 기존의 member와 같은 프로필 주소를 사용할 수 없다
            shouldThrow<DuplicateProfileException> {
                memberRegister.updateInfo(member2Id, MemberInfoUpdateRequest("Kimmy", "kim001", "자기소개임"))
            }

            // 다른 프로필 주소로는 변경 가능
            memberRegister.updateInfo(member2Id, MemberInfoUpdateRequest("Kimmy", "kim002", "자기소개임"))

            // 기존 프로필 주소를 바꾸는 것도 가능
            memberRegister.updateInfo(memberId, MemberInfoUpdateRequest("Kimmy", "kim001", "자기소개임"))

            // 프로필 주소를 제거하는 것도 가능
            memberRegister.updateInfo(memberId, MemberInfoUpdateRequest("Kimmy", "", "자기소개임"))

            // 프로필 주소 중복은 허용하지 않음
            shouldThrow<DuplicateProfileException> {
                memberRegister.updateInfo(memberId, MemberInfoUpdateRequest("Kimmy", "kim002", "자기소개임"))
            }
        }

        test("memberRegisterRequestFail") {
            checkValidation(MemberRegisterRequest("kim@splearn.app", "Kim", "secret1234"))
            checkValidation(MemberRegisterRequest("kim@splearn.app", "KimLongNameSplearnTestCode", "secret1234"))
            checkValidation(MemberRegisterRequest("kimsplearn.app", "KimLongName", "secret1234"))
            checkValidation(MemberRegisterRequest("kim#splearn.app", "KimLongName", "secret"))
        }
    }

    private fun checkValidation(invalid: MemberRegisterRequest) {
        shouldThrow<ConstraintViolationException> { memberRegister.register(invalid) }
    }

    private fun registerMember(): Member = memberRegister.register(MemberFixture.createMemberRegisterRequest())

    private fun registerMember(email: String): Member =
        memberRegister.register(MemberFixture.createMemberRegisterRequest(email))
}
