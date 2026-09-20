package kimspring.splearn.application.enrollment.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.enrollment.EnrollmentNotFoundException
import kimspring.splearn.support.test.BaseApplicationServiceTest
import org.springframework.beans.factory.annotation.Autowired

class EnrollmentFinderTest : BaseApplicationServiceTest() {
    @Autowired
    private lateinit var enrollmentFinder: EnrollmentFinder

    init {
        test("get") {
            val enrollment = prepareEnrollment()

            enrollmentFinder.get(requireNotNull(enrollment.id)) shouldBe enrollment
        }

        test("getFail") {
            shouldThrow<EnrollmentNotFoundException> { enrollmentFinder.get(Long.MAX_VALUE) }
        }

        test("findByMember") {
            val enrollment = prepareEnrollment()

            enrollmentFinder.findByMember(enrollment.memberId).map { it.id } shouldContainExactly listOf(enrollment.id)
            enrollmentFinder.findByMember(Long.MAX_VALUE) shouldBe emptyList()
        }

        test("findByMemberAndCourse") {
            val enrollment = prepareEnrollment()

            enrollmentFinder.findByMemberAndCourse(enrollment.memberId, enrollment.courseId) shouldBe enrollment
            enrollmentFinder.findByMemberAndCourse(enrollment.memberId, Long.MAX_VALUE).shouldBeNull()
        }
    }
}
