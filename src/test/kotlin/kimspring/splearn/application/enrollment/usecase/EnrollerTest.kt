package kimspring.splearn.application.enrollment.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.application.enrollment.command.EnrollCommand
import kimspring.splearn.domain.enrollment.DuplicateEnrollmentException
import kimspring.splearn.domain.enrollment.EnrollmentStatus
import kimspring.splearn.support.test.BaseApplicationServiceTest

class EnrollerTest : BaseApplicationServiceTest() {
    init {
        test("enroll") {
            val member = prepareActiveMember()
            val course = preparePublishedCourse()

            val enrollment = enroller.enroll(EnrollCommand(requireNotNull(member.id), requireNotNull(course.id)))

            enrollment.id.shouldNotBeNull()
        }

        test("enrollFailDuplicate") {
            val enrollment = prepareEnrollment()

            shouldThrow<DuplicateEnrollmentException> {
                enroller.enroll(EnrollCommand(enrollment.memberId, enrollment.courseId))
            }
        }

        test("startStudying") {
            val enrollment = prepareEnrollment()

            val studying = enroller.startStudying(requireNotNull(enrollment.id))

            studying.status shouldBe EnrollmentStatus.STUDYING
        }

        test("complete") {
            val enrollmentId = requireNotNull(prepareEnrollment().id)
            enroller.startStudying(enrollmentId)

            val completed = enroller.complete(enrollmentId)

            completed.status shouldBe EnrollmentStatus.COMPLETED
        }
    }
}
