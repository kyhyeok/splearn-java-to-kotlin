package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.domain.curriculum.Section
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table

@Table("section")
data class SectionJdbcEntity(
    @Id val id: Long? = null,
    val title: String,
    @MappedCollection(idColumn = "section", keyColumn = "lesson_order")
    val lessons: List<LessonJdbcEntity>,
) {
    fun toDomain(): Section = Section(id = id, title = title, lessons = lessons.map { it.toDomain() })

    companion object {
        fun from(section: Section): SectionJdbcEntity =
            SectionJdbcEntity(
                id = section.id,
                title = section.title,
                lessons = section.lessons.map { LessonJdbcEntity.from(it) },
            )
    }
}
