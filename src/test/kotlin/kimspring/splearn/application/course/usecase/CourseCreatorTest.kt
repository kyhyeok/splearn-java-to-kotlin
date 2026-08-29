package kimspring.splearn.application.course.usecase

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.course.CourseFixture
import kimspring.splearn.support.test.BaseApplicationServiceTest

class CourseCreatorTest : BaseApplicationServiceTest() {
    init {
        test("create") {
            val instructor = prepareInstructor()

            val course = courseCreator.create(CourseFixture.createCreateCourseCommand(requireNotNull(instructor.id)))

            course.id.shouldNotBeNull()
        }

        test("updateInfo") {
            val instructor = prepareInstructor()
            val course = courseCreator.create(CourseFixture.createCreateCourseCommand(requireNotNull(instructor.id)))

            val updated =
                courseCreator.updateInfo(
                    requireNotNull(course.id),
                    CourseFixture.createUpdateCourseInfoCommand("Updated"),
                )

            updated.title shouldBe "Updated"
        }
    }
}
