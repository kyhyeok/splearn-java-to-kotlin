package kimspring.splearn.application.instructor.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.instructor.InstructorNotFoundException
import kimspring.splearn.support.test.BaseApplicationServiceTest
import org.springframework.beans.factory.annotation.Autowired

class InstructorFinderTest : BaseApplicationServiceTest() {
    @Autowired
    private lateinit var instructorFinder: InstructorFinder

    init {
        test("get") {
            val instructor = preparePendingInstructor()

            instructorFinder.get(requireNotNull(instructor.id)).id shouldBe instructor.id
        }

        test("getFail") {
            shouldThrow<InstructorNotFoundException> { instructorFinder.get(9999L) }
        }

        test("findByMember") {
            val instructor = preparePendingInstructor()

            instructorFinder.findByMember(instructor.memberId)?.id shouldBe instructor.id

            instructorFinder.findByMember(Long.MAX_VALUE).shouldBeNull()
        }
    }
}
