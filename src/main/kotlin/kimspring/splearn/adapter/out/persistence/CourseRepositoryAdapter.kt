package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.application.course.port.CourseRepository
import kimspring.splearn.domain.course.Course
import kimspring.splearn.domain.course.CourseNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class CourseRepositoryAdapter(
    private val springCourseRepository: SpringCourseRepository,
) : CourseRepository {
    override fun save(course: Course): Course = springCourseRepository.save(CourseJdbcEntity.from(course)).toDomain()

    override fun findById(id: Long): Course? = springCourseRepository.findByIdOrNull(id)?.toDomain()

    override fun getById(id: Long): Course = findById(id) ?: throw CourseNotFoundException(id)

    override fun findByTitleContaining(keyword: String): List<Course> =
        springCourseRepository.findByTitleContaining(keyword).map { it.toDomain() }

    override fun findByInstructorId(instructorId: Long): List<Course> =
        springCourseRepository.findByInstructorId(instructorId).map { it.toDomain() }

    override fun findByInstructorIdAndTitle(
        instructorId: Long,
        title: String,
    ): Course? = springCourseRepository.findByInstructorIdAndTitle(instructorId, title)?.toDomain()
}
