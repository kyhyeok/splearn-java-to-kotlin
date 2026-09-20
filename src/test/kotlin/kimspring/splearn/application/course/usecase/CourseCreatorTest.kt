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

            val courseId = course.id.shouldNotBeNull()

            // 강의를 만들면 빈 커리큘럼이 함께 만들어진다
            val curriculum = curriculumFinder.findByCourse(courseId)
            curriculum.id.shouldNotBeNull()
            curriculum.courseId shouldBe courseId
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
