package kimspring.splearn.domain.course

import kimspring.splearn.application.course.command.CreateCourseCommand
import kimspring.splearn.application.course.command.UpdateCourseInfoCommand
import kimspring.splearn.domain.instructor.Instructor
import kimspring.splearn.domain.instructor.InstructorFixture
import org.instancio.Instancio
import org.instancio.Select.field
import java.time.LocalDateTime

object CourseFixture {
    private val FIXED_NOW = LocalDateTime.of(2024, 1, 1, 0, 0)

    fun createCourse(
        instructor: Instructor = InstructorFixture.createActiveInstructor(),
        title: String = randomTitle(),
        description: String? = null,
    ): Course = Course.create(instructor, title, description, FIXED_NOW)

    fun createCreateCourseCommand(
        instructorId: Long,
        title: String = randomTitle(),
    ): CreateCourseCommand =
        Instancio
            .of(CreateCourseCommand::class.java)
            .set(field(CreateCourseCommand::class.java, CreateCourseCommand::instructorId.name), instructorId)
            .set(field(CreateCourseCommand::class.java, CreateCourseCommand::title.name), title)
            .create()

    fun createUpdateCourseInfoCommand(title: String = randomTitle()): UpdateCourseInfoCommand =
        Instancio
            .of(UpdateCourseInfoCommand::class.java)
            .set(field(UpdateCourseInfoCommand::class.java, UpdateCourseInfoCommand::title.name), title)
            .create()

    private fun randomTitle(): String =
        Instancio
            .gen()
            .string()
            .minLength(100)
            .maxLength(100)
            .get()
}
