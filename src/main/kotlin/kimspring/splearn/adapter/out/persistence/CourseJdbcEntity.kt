package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.domain.course.Course
import kimspring.splearn.domain.course.CourseStatus
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table("course")
data class CourseJdbcEntity(
    @Id val id: Long? = null,
    @Version val version: Long? = null,
    val instructorId: Long,
    val title: String,
    val status: CourseStatus,
    // 1:1 detail 은 루트 재저장 시 삭제 후 재삽입된다. MemberJdbcEntity 의 같은 주석 참고
    val detail: CourseDetailJdbcEntity,
) {
    fun toDomain(): Course =
        Course(
            id = id,
            instructorId = instructorId,
            title = title,
            status = status,
            detail = detail.toDomain(),
            version = version,
        )

    companion object {
        fun from(course: Course): CourseJdbcEntity =
            CourseJdbcEntity(
                id = course.id,
                instructorId = course.instructorId,
                title = course.title,
                status = course.status,
                detail = CourseDetailJdbcEntity.from(course.detail),
                version = course.version,
            )
    }
}
