package kimspring.splearn.application.course

import kimspring.splearn.application.course.command.CreateCourseCommand
import kimspring.splearn.application.course.command.UpdateCourseInfoCommand
import kimspring.splearn.application.course.port.CourseRepository
import kimspring.splearn.application.course.usecase.CourseCreator
import kimspring.splearn.application.course.usecase.CoursePublisher
import kimspring.splearn.application.course.usecase.CourseValidator
import kimspring.splearn.application.instructor.usecase.InstructorFinder
import kimspring.splearn.domain.course.Course
import kimspring.splearn.domain.shared.Clock
import kimspring.splearn.support.stereotype.ApplicationService

@ApplicationService
class CourseModifyService(
    private val courseRepository: CourseRepository,
    private val courseValidator: CourseValidator,
    private val instructorFinder: InstructorFinder,
    private val clock: Clock,
) : CourseCreator,
    CoursePublisher {
    override fun create(command: CreateCourseCommand): Course {
        val instructor = instructorFinder.find(command.instructorId)
        courseValidator.validateForCreate(instructor, command)
        return courseRepository.save(Course.create(instructor, command.title, command.description, clock.now()))
    }

    override fun updateInfo(
        courseId: Long,
        command: UpdateCourseInfoCommand,
    ): Course {
        val course = courseRepository.getById(courseId)
        courseValidator.validateForUpdate(course, command)
        return courseRepository.save(course.updateInfo(command.title, command.description))
    }

    override fun submitForReview(courseId: Long): Course =
        courseRepository.save(courseRepository.getById(courseId).submitForReview())

    override fun publish(courseId: Long): Course =
        courseRepository.save(courseRepository.getById(courseId).publish(clock.now()))

    override fun archive(courseId: Long): Course =
        courseRepository.save(courseRepository.getById(courseId).archive(clock.now()))
}
