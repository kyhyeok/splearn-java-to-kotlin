package kimspring.splearn.application.course

import kimspring.splearn.application.course.command.CreateCourseCommand
import kimspring.splearn.application.course.command.UpdateCourseInfoCommand
import kimspring.splearn.application.course.port.CourseRepository
import kimspring.splearn.application.course.usecase.CourseValidator
import kimspring.splearn.domain.course.Course
import kimspring.splearn.domain.course.CourseValidationException
import kimspring.splearn.domain.instructor.Instructor
import kimspring.splearn.support.stereotype.ApplicationService

@ApplicationService
class CourseValidationService(
    private val courseRepository: CourseRepository,
) : CourseValidator {
    override fun validateForCreate(
        instructor: Instructor,
        command: CreateCourseCommand,
    ) {
        instructor.ensureActive()
        val instructorId = requireNotNull(instructor.id) { "저장되지 않은 강사입니다." }
        if (courseRepository.findByInstructorIdAndTitle(instructorId, command.title) != null) {
            throw CourseValidationException(listOf("이미 사용중인 강의 제목입니다. ${command.title}"))
        }
    }

    override fun validateForUpdate(
        course: Course,
        command: UpdateCourseInfoCommand,
    ) {
        val found = courseRepository.findByInstructorIdAndTitle(course.instructorId, command.title)
        if (found != null && found.id != course.id) {
            throw CourseValidationException(listOf("이미 사용중인 강의 제목입니다. ${command.title}"))
        }
    }
}
