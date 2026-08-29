package kimspring.splearn.application.course.usecase

import kimspring.splearn.domain.course.Course

/**
 * 강의를 조회한다
 */
interface CourseFinder {
    fun find(courseId: Long): Course

    fun findByTitle(keyword: String): List<Course>

    fun findByInstructor(instructorId: Long): List<Course>
}
