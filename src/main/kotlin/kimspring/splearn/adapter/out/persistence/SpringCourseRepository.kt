package kimspring.splearn.adapter.out.persistence

import org.springframework.data.repository.CrudRepository

interface SpringCourseRepository : CrudRepository<CourseJdbcEntity, Long> {
    fun findByTitleContaining(keyword: String): List<CourseJdbcEntity>

    fun findByInstructorId(instructorId: Long): List<CourseJdbcEntity>

    fun findByInstructorIdAndTitle(
        instructorId: Long,
        title: String,
    ): CourseJdbcEntity?
}
