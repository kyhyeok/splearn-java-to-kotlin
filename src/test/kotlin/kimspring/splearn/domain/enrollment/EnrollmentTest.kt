package kimspring.splearn.domain.enrollment

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.course.CourseFixture
import kimspring.splearn.domain.course.InvalidCourseStateException
import kimspring.splearn.domain.member.InvalidMemberStateException
import kimspring.splearn.domain.member.MemberFixture
import java.time.LocalDateTime

class EnrollmentTest :
    FunSpec({
        val now = LocalDateTime.of(2024, 1, 1, 0, 0)

        test("enroll") {
            val member = MemberFixture.createActiveMember()
            val course = CourseFixture.createPublishedCourse()

            val enrollment = Enrollment.enroll(member, course, now)

            enrollment.memberId shouldBe member.id
            enrollment.courseId shouldBe course.id
            enrollment.status shouldBe EnrollmentStatus.ENROLLED
            enrollment.enrolledAt shouldBe now
        }

        test("enrollFailNotActiveMember") {
            val member = MemberFixture.createMember(1L) // PENDING
            val course = CourseFixture.createPublishedCourse()

            shouldThrow<InvalidMemberStateException> { Enrollment.enroll(member, course, now) }
        }

        test("enrollFailNotPublishedCourse") {
            val member = MemberFixture.createActiveMember()
            val course = CourseFixture.createCourse() // DRAFT

            shouldThrow<InvalidCourseStateException> { Enrollment.enroll(member, course, now) }
        }

        test("startStudying") {
            val enrollment = EnrollmentFixture.createEnrollment()

            val studying = enrollment.startStudying()

            studying.status shouldBe EnrollmentStatus.STUDYING

            shouldThrow<InvalidEnrollmentStateException> { studying.startStudying() }
        }

        test("complete") {
            val studying = EnrollmentFixture.createEnrollment().startStudying()

            val completed = studying.complete(now)

            completed.status shouldBe EnrollmentStatus.COMPLETED
            completed.completedAt shouldBe now

            shouldThrow<InvalidEnrollmentStateException> { completed.complete(now) }
        }
    })
