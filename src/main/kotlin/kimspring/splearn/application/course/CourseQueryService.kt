package kimspring.splearn.application.course

import kimspring.splearn.application.course.port.CourseRepository
import kimspring.splearn.application.course.usecase.CourseFinder
import kimspring.splearn.domain.course.Course
import kimspring.splearn.support.stereotype.QueryApplicationService

@QueryApplicationService
class CourseQueryService(
    private val courseRepository: CourseRepository,
) : CourseFinder {
    override fun find(courseId: Long): Course = courseRepository.getById(courseId)

    override fun findByTitle(keyword: String): List<Course> = courseRepository.findByTitleContaining(keyword)

    override fun findByInstructor(instructorId: Long): List<Course> = courseRepository.findByInstructorId(instructorId)
}
