package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.domain.course.CourseDetail
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("course_detail")
data class CourseDetailJdbcEntity(
    @Id val id: Long? = null,
    val description: String? = null,
    val createdAt: LocalDateTime,
    val publishedAt: LocalDateTime? = null,
    val archivedAt: LocalDateTime? = null,
) {
    fun toDomain(): CourseDetail =
        CourseDetail(
            description = description,
            createdAt = createdAt,
            publishedAt = publishedAt,
            archivedAt = archivedAt,
        )

    companion object {
        fun from(detail: CourseDetail): CourseDetailJdbcEntity =
            CourseDetailJdbcEntity(
                description = detail.description,
                createdAt = detail.createdAt,
                publishedAt = detail.publishedAt,
                archivedAt = detail.archivedAt,
            )
    }
}
