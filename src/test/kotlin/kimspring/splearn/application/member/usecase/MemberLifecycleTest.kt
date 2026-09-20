package kimspring.splearn.application.member.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.member.InvalidActivationTokenException
import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberFixture
import kimspring.splearn.domain.member.MemberStatus
import kimspring.splearn.support.test.BaseApplicationServiceTest

class MemberLifecycleTest : BaseApplicationServiceTest() {
    init {
        test("activate") {
            val member = registerMember()

            val activated = memberLifecycle.activate(requireNotNull(member.activationToken))

            activated.status shouldBe MemberStatus.ACTIVE
            activated.detail.activatedAt.shouldNotBeNull()
        }

        test("activateFailInvalidToken") {
            registerMember()

            shouldThrow<InvalidActivationTokenException> { memberLifecycle.activate("no-such-token") }
        }

        test("activateFailReusedToken") {
            val member = registerMember()
            val token = requireNotNull(member.activationToken)
            memberLifecycle.activate(token)

            // 1회용 — 활성화 시 토큰이 비워지므로 같은 토큰으로 다시 찾을 수 없다
            shouldThrow<InvalidActivationTokenException> { memberLifecycle.activate(token) }
        }

        test("deactivate") {
            val activated = prepareActiveMember()

            val deactivated = memberLifecycle.deactivate(requireNotNull(activated.id))

            deactivated.status shouldBe MemberStatus.DEACTIVATED
            deactivated.detail.deactivatedAt.shouldNotBeNull()
        }
    }

    private fun registerMember(): Member = memberRegister.register(MemberFixture.createRegisterMemberCommand())
}
