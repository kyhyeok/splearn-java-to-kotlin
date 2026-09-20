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
    // Spring Data JDBC 는 루트 재저장 시 섹션·수업 행을 전부 삭제 후 재삽입한다(수업 1개 추가 = JDBC 호출 6회, id 는 보존).
    // 현재 규모에서는 수용한다. 편집 빈도가 높아지면 Section/Lesson 을 별도 애그리거트로 분리하거나 변경분만 UPDATE 하는 SQL 을 둔다
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
