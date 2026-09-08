package kimspring.splearn.application.enrollment

import kimspring.splearn.application.enrollment.port.EnrollmentRepository
import kimspring.splearn.application.enrollment.usecase.EnrollmentFinder
import kimspring.splearn.domain.enrollment.Enrollment
import kimspring.splearn.support.stereotype.QueryApplicationService

@QueryApplicationService
class EnrollmentQueryService(
    private val enrollmentRepository: EnrollmentRepository,
) : EnrollmentFinder {
    override fun find(enrollmentId: Long): Enrollment = enrollmentRepository.getById(enrollmentId)

    override fun findByMember(memberId: Long): List<Enrollment> = enrollmentRepository.findByMemberId(memberId)

    override fun findByMemberAndCourse(
        memberId: Long,
        courseId: Long,
    ): Enrollment? = enrollmentRepository.findByMemberIdAndCourseId(memberId, courseId)
}
