package kimspring.splearn.adapter.out.persistence

import org.springframework.data.repository.CrudRepository

interface SpringEnrollmentRepository : CrudRepository<EnrollmentJdbcEntity, Long> {
    fun findByMemberId(memberId: Long): List<EnrollmentJdbcEntity>

    fun findByMemberIdAndCourseId(
        memberId: Long,
        courseId: Long,
    ): EnrollmentJdbcEntity?
}
