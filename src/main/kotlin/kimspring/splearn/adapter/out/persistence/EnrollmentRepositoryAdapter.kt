package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.application.enrollment.port.EnrollmentRepository
import kimspring.splearn.domain.enrollment.Enrollment
import kimspring.splearn.domain.enrollment.EnrollmentNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class EnrollmentRepositoryAdapter(
    private val springEnrollmentRepository: SpringEnrollmentRepository,
) : EnrollmentRepository {
    override fun save(enrollment: Enrollment): Enrollment =
        springEnrollmentRepository.save(EnrollmentJdbcEntity.from(enrollment)).toDomain()

    override fun findById(id: Long): Enrollment? = springEnrollmentRepository.findByIdOrNull(id)?.toDomain()

    override fun getById(id: Long): Enrollment = findById(id) ?: throw EnrollmentNotFoundException(id)

    override fun findByMemberId(memberId: Long): List<Enrollment> =
        springEnrollmentRepository.findByMemberId(memberId).map { it.toDomain() }

    override fun findByMemberIdAndCourseId(
        memberId: Long,
        courseId: Long,
    ): Enrollment? = springEnrollmentRepository.findByMemberIdAndCourseId(memberId, courseId)?.toDomain()
}
