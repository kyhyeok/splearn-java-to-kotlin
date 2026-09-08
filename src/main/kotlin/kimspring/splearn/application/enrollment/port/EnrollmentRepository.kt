package kimspring.splearn.application.enrollment.port

import kimspring.splearn.domain.enrollment.Enrollment

interface EnrollmentRepository {
    fun save(enrollment: Enrollment): Enrollment

    fun findById(id: Long): Enrollment?

    fun getById(id: Long): Enrollment

    fun findByMemberId(memberId: Long): List<Enrollment>

    fun findByMemberIdAndCourseId(
        memberId: Long,
        courseId: Long,
    ): Enrollment?
}
