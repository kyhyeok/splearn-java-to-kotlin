package kimspring.splearn.domain.enrollment

import kimspring.splearn.domain.course.Course
import kimspring.splearn.domain.course.CourseFixture
import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberFixture
import java.time.LocalDateTime

object EnrollmentFixture {
    private val FIXED_NOW = LocalDateTime.of(2024, 1, 1, 0, 0)

    fun createEnrollment(
        member: Member = MemberFixture.createActiveMember(),
        course: Course = CourseFixture.createPublishedCourse(),
    ): Enrollment = Enrollment.enroll(member, course, FIXED_NOW)
}
