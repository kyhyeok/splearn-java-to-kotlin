package kimspring.splearn.application.instructor.port

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.instructor.InstructorFixture
import kimspring.splearn.domain.instructor.InstructorNotFoundException
import kimspring.splearn.support.test.BaseRepositoryTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.OptimisticLockingFailureException

class InstructorRepositoryTest : BaseRepositoryTest() {
    init {
        test("saveAndFindById") {
            val saved = prepareActiveInstructor()

            val id = saved.id.shouldNotBeNull()
            instructorRepository.findById(id) shouldBe saved
        }

        test("getByIdFail") {
            shouldThrow<InstructorNotFoundException> { instructorRepository.getById(Long.MAX_VALUE) }
        }

        test("findByMemberId") {
            val saved = prepareActiveInstructor()

            instructorRepository.findByMemberId(saved.memberId) shouldBe saved
            instructorRepository.findByMemberId(Long.MAX_VALUE).shouldBeNull()
        }

        test("uniqueMember") {
            val member = prepareActiveMember()
            prepareActiveInstructor(member)

            shouldThrow<DataIntegrityViolationException> {
                instructorRepository.save(InstructorFixture.createInstructor(member))
            }
        }

        test("staleSaveFailsWithOptimisticLock") {
            val pending = instructorRepository.save(InstructorFixture.createInstructor(prepareActiveMember()))
            val id = requireNotNull(pending.id)
            val first = instructorRepository.getById(id)
            val second = instructorRepository.getById(id)

            instructorRepository.save(first.approve())

            shouldThrow<OptimisticLockingFailureException> {
                instructorRepository.save(second.reject())
            }
        }
    }
}
