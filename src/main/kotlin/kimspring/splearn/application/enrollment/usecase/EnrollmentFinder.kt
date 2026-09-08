package kimspring.splearn.application.enrollment.usecase

import kimspring.splearn.domain.enrollment.Enrollment

/**
 * 수강을 조회한다
 */
interface EnrollmentFinder {
    fun find(enrollmentId: Long): Enrollment

    fun findByMember(memberId: Long): List<Enrollment>

    fun findByMemberAndCourse(
        memberId: Long,
        courseId: Long,
    ): Enrollment?
}
