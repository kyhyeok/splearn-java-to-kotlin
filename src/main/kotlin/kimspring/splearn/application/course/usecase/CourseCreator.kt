package kimspring.splearn.application.course.usecase

import jakarta.validation.Valid
import kimspring.splearn.application.course.command.CreateCourseCommand
import kimspring.splearn.application.course.command.UpdateCourseInfoCommand
import kimspring.splearn.domain.course.Course

/**
 * 강의를 준비하는 작업
 */
interface CourseCreator {
    fun create(
        @Valid command: CreateCourseCommand,
    ): Course

    fun updateInfo(
        courseId: Long,
        @Valid command: UpdateCourseInfoCommand,
    ): Course
}
