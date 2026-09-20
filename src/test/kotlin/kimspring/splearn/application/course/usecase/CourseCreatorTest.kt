package kimspring.splearn.application.course.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.course.CourseFixture
import kimspring.splearn.domain.instructor.InstructorNotFoundException
import kimspring.splearn.domain.instructor.InvalidInstructorStateException
import kimspring.splearn.support.test.BaseApplicationServiceTest

class CourseCreatorTest : BaseApplicationServiceTest() {
    init {
        test("create") {
            val instructor = prepareInstructor()

            val course = courseCreator.create(CourseFixture.createCreateCourseCommand(requireNotNull(instructor.id)))

            val courseId = course.id.shouldNotBeNull()

            // 강의를 만들면 빈 커리큘럼이 함께 만들어진다
            val curriculum = curriculumFinder.getByCourse(courseId)
            curriculum.id.shouldNotBeNull()
            curriculum.courseId shouldBe courseId
        }

        test("createFailInstructorNotFound") {
            shouldThrow<InstructorNotFoundException> {
                courseCreator.create(CourseFixture.createCreateCourseCommand(Long.MAX_VALUE))
            }
        }

        // 강사 상태 규칙은 Course.create 에만 있다. 검증 서비스가 먼저 막지 않아도 도메인이 거절해야 한다
        test("createFailPendingInstructor") {
            val instructor = preparePendingInstructor()

            shouldThrow<InvalidInstructorStateException> {
                courseCreator.create(CourseFixture.createCreateCourseCommand(requireNotNull(instructor.id)))
            }
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
