package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.domain.course.Course
import kimspring.splearn.domain.course.CourseStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("course")
data class CourseJdbcEntity(
    @Id val id: Long? = null,
    val instructorId: Long,
    val title: String,
    val status: CourseStatus,
    val detail: CourseDetailJdbcEntity,
) {
    fun toDomain(): Course =
        Course(
            id = id,
            instructorId = instructorId,
            title = title,
            status = status,
            detail = detail.toDomain(),
        )

    companion object {
        fun from(course: Course): CourseJdbcEntity =
            CourseJdbcEntity(
                id = course.id,
                instructorId = course.instructorId,
                title = course.title,
                status = course.status,
                detail = CourseDetailJdbcEntity.from(course.detail),
            )
    }
}
