package kimspring.splearn.application.member.port

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.member.MemberFixture.createMember
import kimspring.splearn.domain.member.MemberStatus
import kimspring.splearn.support.test.BaseRepositoryTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.OptimisticLockingFailureException
import java.time.LocalDateTime

class MemberRepositoryTest : BaseRepositoryTest() {
    private val now = LocalDateTime.of(2024, 1, 1, 0, 0)

    init {
        test("registerMember") {
            val member = createMember()

            member.id.shouldBeNull()

            val saved = memberRepository.save(member)

            val id = saved.id.shouldNotBeNull()

            val found =
                memberRepository.findById(id)
                    ?: throw NoSuchElementException()
            found.status shouldBe MemberStatus.PENDING
            found.detail.registeredAt.shouldNotBeNull()
        }

        test("findByActivationToken") {
            val saved = memberRepository.save(createMember())
            val token = saved.activationToken.shouldNotBeNull()

            memberRepository.findByActivationToken(token).shouldNotBeNull().id shouldBe saved.id
            memberRepository.findByActivationToken("no-such-token").shouldBeNull()
        }

        test("duplicateEmailFail") {
            val member = memberRepository.save(createMember())

            shouldThrow<DataIntegrityViolationException> {
                memberRepository.save(createMember(member.email.address))
            }
        }

        // 같은 버전을 읽은 두 전이 중 나중 저장은 앞선 결과를 덮어쓰지 않고 실패해야 한다
        test("staleSaveFailsWithOptimisticLock") {
            val id = requireNotNull(memberRepository.save(createMember()).id)
            val first = memberRepository.getById(id)
            val second = memberRepository.getById(id)

            memberRepository.save(first.activate(now))

            shouldThrow<OptimisticLockingFailureException> {
                memberRepository.save(second.activate(now))
            }
        }
    }
}
