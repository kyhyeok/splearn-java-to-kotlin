package kimspring.splearn.application.instructor.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kimspring.splearn.application.instructor.port.InstructorRepository
import kimspring.splearn.domain.instructor.DuplicateInstructorApplicationException
import kimspring.splearn.domain.instructor.InstructorStatus
import kimspring.splearn.support.test.BaseApplicationServiceTest
import org.springframework.beans.factory.annotation.Autowired

class InstructorApplicationTest : BaseApplicationServiceTest() {
    @Autowired
    private lateinit var instructorRepository: InstructorRepository

    init {
        test("apply") {
            val member = prepareMember()

            val instructor = instructorApplication.apply(requireNotNull(member.id))

            instructor.status shouldBe InstructorStatus.PENDING
            instructorRepository.getById(requireNotNull(instructor.id)).memberId shouldBe member.id
        }

        test("applyDuplicateFail") {
            val member = prepareMember()
            instructorApplication.apply(requireNotNull(member.id))

            shouldThrow<DuplicateInstructorApplicationException> {
                instructorApplication.apply(requireNotNull(member.id))
            }
        }

        test("approve") {
            val instructor = preparePendingInstructor()

            val approved = instructorApplication.approve(requireNotNull(instructor.id))

            approved.status shouldBe InstructorStatus.ACTIVE
        }

        test("reject") {
            val instructor = preparePendingInstructor()

            val rejected = instructorApplication.reject(requireNotNull(instructor.id))

            rejected.status shouldBe InstructorStatus.REJECTED
        }
    }
}
