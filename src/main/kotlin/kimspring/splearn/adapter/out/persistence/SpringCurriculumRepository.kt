package kimspring.splearn.adapter.out.persistence

import org.springframework.data.repository.CrudRepository

interface SpringCurriculumRepository : CrudRepository<CurriculumJdbcEntity, Long> {
    fun findByCourseId(courseId: Long): CurriculumJdbcEntity?
}
