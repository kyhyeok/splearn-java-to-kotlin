package kimspring.splearn.domain.member

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.member.MemberFixture.createPasswordEncoder
import kimspring.splearn.domain.member.MemberFixture.createRegisterMemberCommand
import kimspring.splearn.domain.shared.Email

class MemberTest : FunSpec() {
    private lateinit var member: Member
    private lateinit var passwordEncoder: PasswordEncoder

    init {
        beforeEach {
            passwordEncoder = createPasswordEncoder()
            val command = createRegisterMemberCommand()
            member = Member.register(Email(command.email), command.nickname, command.password, passwordEncoder)
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
                Member.register(Email("invaluid email"), "KimHyeok", "verysecret", passwordEncoder)
            }

            Member.register(Email("kim@gmail.com"), "KimHyeok", "verysecret", passwordEncoder)
        }

        test("updateInfo") {
            member = member.activate()

            member = member.updateInfo("Hyeok", "kim001", "자기소개")

            member.nickname shouldBe "Hyeok"
            member.detail.profile!!.address shouldBe "kim001"
            member.detail.introduction shouldBe "자기소개"
        }

        test("updateInfoFail") {
            shouldThrow<IllegalStateException> {
                member.updateInfo("Hyeok", "kim001", "자기소개")
            }
        }
    }
}
