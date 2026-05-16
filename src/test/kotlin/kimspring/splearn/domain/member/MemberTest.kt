package kimspring.splearn.domain.member

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.member.MemberFixture.createMemberRegisterRequest
import kimspring.splearn.domain.member.MemberFixture.createPasswordEncoder

class MemberTest : FunSpec() {
    private lateinit var member: Member
    private lateinit var passwordEncoder: PasswordEncoder

    init {
        beforeEach {
            passwordEncoder = createPasswordEncoder()
            member = Member.register(createMemberRegisterRequest(), passwordEncoder)
        }

        test("registerMember") {
            member.status shouldBe MemberStatus.PENDING
            member.detail.registeredAt.shouldNotBeNull()
        }

        test("activate") {
            member = member.activate()
            member.status shouldBe MemberStatus.ACTIVE
        }

        test("activateFail") {
            member.detail.activatedAt.shouldBeNull()

            member = member.activate()

            shouldThrow<IllegalStateException> {
                member.activate()
            }

            member.detail.activatedAt.shouldNotBeNull()
        }

        test("deactivate") {
            member = member.activate()
            member = member.deactivate()

            member.status shouldBe MemberStatus.DEACTIVATED
            member.detail.deactivatedAt.shouldNotBeNull()
        }

        test("deactivateFail") {
            shouldThrow<IllegalStateException> { member.deactivate() }

            member = member.activate()
            member = member.deactivate()
            shouldThrow<IllegalStateException> { member.deactivate() }
        }

        test("verifyPassword") {
            member.verifyPassword("verysecret", passwordEncoder) shouldBe true
            member.verifyPassword("hello", passwordEncoder) shouldBe false
        }

        test("changePassword") {
            member = member.changePassword("verysecret2", passwordEncoder)
            member.verifyPassword("verysecret2", passwordEncoder) shouldBe true
        }

        test("isActive") {
            member.isActive() shouldBe false
            member = member.activate()
            member.isActive() shouldBe true
            member = member.deactivate()
            member.isActive() shouldBe false
        }

        test("invalidEmail") {
            shouldThrow<IllegalArgumentException> {
                Member.register(createMemberRegisterRequest("invaluid email"), passwordEncoder)
            }

            Member.register(createMemberRegisterRequest(), passwordEncoder)
        }

        test("updateInfo") {
            member = member.activate()

            val request = MemberInfoUpdateRequest("Hyeok", "kim001", "자기소개")
            member = member.updateInfo(request)

            member.nickname shouldBe request.nickname
            member.detail.profile!!.address shouldBe request.profileAddress
            member.detail.introduction shouldBe request.introduction
        }

        test("updateInfoFail") {
            shouldThrow<IllegalStateException> {
                val request = MemberInfoUpdateRequest("Hyeok", "kim001", "자기소개")
                member.updateInfo(request)
            }
        }
    }
}
