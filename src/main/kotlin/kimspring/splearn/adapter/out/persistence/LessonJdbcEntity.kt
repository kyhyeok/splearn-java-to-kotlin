package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.domain.curriculum.Lesson
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("lesson")
data class LessonJdbcEntity(
    @Id val id: Long? = null,
    val title: String,
) {
    fun toDomain(): Lesson = Lesson(id = id, title = title)

    companion object {
        fun from(lesson: Lesson): LessonJdbcEntity = LessonJdbcEntity(id = lesson.id, title = lesson.title)
    }
}
