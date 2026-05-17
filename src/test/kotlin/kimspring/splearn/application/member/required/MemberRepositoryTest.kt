package kimspring.splearn.application.member.required

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.member.MemberFixture.createMember
import kimspring.splearn.domain.member.MemberStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class MemberRepositoryTest : FunSpec() {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    init {
        extension(SpringExtension())

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

        test("duplicateEmailFail") {
            memberRepository.save(createMember())

            shouldThrow<DataIntegrityViolationException> { memberRepository.save(createMember()) }
        }
    }
}
