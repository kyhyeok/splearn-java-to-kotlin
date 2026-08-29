package kimspring.splearn.application.course.usecase

import kimspring.splearn.application.course.command.CreateCourseCommand
import kimspring.splearn.application.course.command.UpdateCourseInfoCommand
import kimspring.splearn.domain.course.Course
import kimspring.splearn.domain.instructor.Instructor

/**
 * 강의 정보가 도메인 밖의 조건(중복 등)을 만족하는지 검증한다
 */
interface CourseValidator {
    fun validateForCreate(
        instructor: Instructor,
        command: CreateCourseCommand,
    )

    fun validateForUpdate(
        course: Course,
        command: UpdateCourseInfoCommand,
    )
}
