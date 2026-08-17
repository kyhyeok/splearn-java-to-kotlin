package kimspring.splearn.application.instructor.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import kimspring.splearn.SplearnTestConfiguration
import kimspring.splearn.application.instructor.port.InstructorRepository
import kimspring.splearn.application.member.usecase.MemberLifecycle
import kimspring.splearn.application.member.usecase.MemberRegister
import kimspring.splearn.domain.instructor.DuplicateInstructorApplicationException
import kimspring.splearn.domain.instructor.Instructor
import kimspring.splearn.domain.instructor.InstructorStatus
import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberFixture
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Import(SplearnTestConfiguration::class)
class InstructorApplicationTest : FunSpec() {
    @Autowired
    private lateinit var instructorApplication: InstructorApplication

    @Autowired
    private lateinit var instructorRepository: InstructorRepository

    @Autowired
    private lateinit var memberRegister: MemberRegister

    @Autowired
    private lateinit var memberLifecycle: MemberLifecycle

    init {
        extension(SpringExtension())

        test("apply") {
            val member = prepareActiveMember()

            val instructor = instructorApplication.apply(member.id!!)

            instructor.status shouldBe InstructorStatus.PENDING
            instructorRepository.getById(instructor.id!!).memberId shouldBe member.id
        }

        test("applyDuplicateFail") {
            val member = prepareActiveMember()
            instructorApplication.apply(member.id!!)

            shouldThrow<DuplicateInstructorApplicationException> {
                instructorApplication.apply(member.id!!)
            }
        }

        test("approve") {
            val instructor = preparePendingInstructor()

            val approved = instructorApplication.approve(instructor.id!!)

            approved.status shouldBe InstructorStatus.ACTIVE
        }

        test("reject") {
            val instructor = preparePendingInstructor()

            val rejected = instructorApplication.reject(instructor.id!!)

            rejected.status shouldBe InstructorStatus.REJECTED
        }
    }

    private fun prepareActiveMember(): Member {
        val member = memberRegister.register(MemberFixture.createRegisterMemberCommand())
        return memberLifecycle.activate(member.id!!)
    }

    private fun preparePendingInstructor(): Instructor = instructorApplication.apply(prepareActiveMember().id!!)
}
