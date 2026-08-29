package kimspring.splearn.application.course.port

import kimspring.splearn.domain.course.Course

interface CourseRepository {
    fun save(course: Course): Course

    fun findById(id: Long): Course?

    fun getById(id: Long): Course

    fun findByTitleContaining(keyword: String): List<Course>

    fun findByInstructorId(instructorId: Long): List<Course>

    fun findByInstructorIdAndTitle(
        instructorId: Long,
        title: String,
    ): Course?
}
