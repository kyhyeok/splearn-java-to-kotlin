package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.domain.enrollment.Enrollment
import kimspring.splearn.domain.enrollment.EnrollmentStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("enrollment")
data class EnrollmentJdbcEntity(
    @Id val id: Long? = null,
    val memberId: Long,
    val courseId: Long,
    val status: EnrollmentStatus,
    val enrolledAt: LocalDateTime,
    val completedAt: LocalDateTime? = null,
) {
    fun toDomain(): Enrollment =
        Enrollment(
            id = id,
            memberId = memberId,
            courseId = courseId,
            status = status,
            enrolledAt = enrolledAt,
            completedAt = completedAt,
        )

    companion object {
        fun from(enrollment: Enrollment): EnrollmentJdbcEntity =
            EnrollmentJdbcEntity(
                id = enrollment.id,
                memberId = enrollment.memberId,
                courseId = enrollment.courseId,
                status = enrollment.status,
                enrolledAt = enrollment.enrolledAt,
                completedAt = enrollment.completedAt,
            )
    }
}
