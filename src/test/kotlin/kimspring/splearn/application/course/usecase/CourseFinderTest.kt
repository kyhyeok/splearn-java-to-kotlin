package kimspring.splearn.application.course.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.course.CourseFixture
import kimspring.splearn.domain.course.CourseNotFoundException
import kimspring.splearn.support.test.BaseApplicationServiceTest
import org.springframework.beans.factory.annotation.Autowired

class CourseFinderTest : BaseApplicationServiceTest() {
    @Autowired
    private lateinit var courseFinder: CourseFinder

    init {
        test("get") {
            val course = prepareCourse()

            courseFinder.get(requireNotNull(course.id)) shouldBe course
        }

        test("getFail") {
            shouldThrow<CourseNotFoundException> { courseFinder.get(Long.MAX_VALUE) }
        }

        test("findByTitle") {
            val instructorId = requireNotNull(prepareInstructor().id)
            val hello = courseCreator.create(CourseFixture.createCreateCourseCommand(instructorId, "Hello Spring"))
            courseCreator.create(CourseFixture.createCreateCourseCommand(instructorId, "Clean Code"))

            courseFinder.findByTitle("Spring").map { it.id } shouldContainExactly listOf(hello.id)
            courseFinder.findByTitle("JPA") shouldBe emptyList()
        }

        test("findByInstructor") {
            val instructorId = requireNotNull(prepareInstructor().id)
            val otherInstructorId = requireNotNull(prepareInstructor().id)
            val course = courseCreator.create(CourseFixture.createCreateCourseCommand(instructorId))
            courseCreator.create(CourseFixture.createCreateCourseCommand(otherInstructorId))

            courseFinder.findByInstructor(instructorId).map { it.id } shouldContainExactly listOf(course.id)
            courseFinder.findByInstructor(Long.MAX_VALUE) shouldBe emptyList()
        }
    }
}
