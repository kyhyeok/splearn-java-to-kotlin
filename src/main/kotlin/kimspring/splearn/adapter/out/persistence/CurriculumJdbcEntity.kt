package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.domain.curriculum.Curriculum
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table

@Table("curriculum")
data class CurriculumJdbcEntity(
    @Id val id: Long? = null,
    @Version val version: Long? = null,
    val courseId: Long,
    @MappedCollection(idColumn = "curriculum", keyColumn = "section_order")
    val sections: List<SectionJdbcEntity>,
) {
    fun toDomain(): Curriculum =
        Curriculum(id = id, courseId = courseId, sections = sections.map { it.toDomain() }, version = version)

    companion object {
        fun from(curriculum: Curriculum): CurriculumJdbcEntity =
            CurriculumJdbcEntity(
                id = curriculum.id,
                version = curriculum.version,
                courseId = curriculum.courseId,
                sections = curriculum.sections.map { SectionJdbcEntity.from(it) },
            )
    }
}
