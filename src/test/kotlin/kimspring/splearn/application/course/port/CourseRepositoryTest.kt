package kimspring.splearn.application.course.port

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.course.CourseFixture
import kimspring.splearn.support.test.BaseRepositoryTest
import org.springframework.dao.DataIntegrityViolationException

class CourseRepositoryTest : BaseRepositoryTest() {
    init {
        test("saveAndFindById") {
            val saved = prepareCourse()

            val id = saved.id.shouldNotBeNull()
            courseRepository.findById(id) shouldBe saved
        }

        test("findByTitleContaining") {
            val instructor = prepareActiveInstructor()
            val hello = prepareCourse(instructor, "Hello Spring")
            val cleanSpring = prepareCourse(instructor, "Clean Spring 2")
            val cleanCode = prepareCourse(instructor, "Clean Code")

            courseRepository.findByTitleContaining("Spring") shouldContainExactlyInAnyOrder listOf(hello, cleanSpring)
            courseRepository.findByTitleContaining("Clean") shouldContainExactlyInAnyOrder
                listOf(cleanSpring, cleanCode)
            courseRepository.findByTitleContaining("Code") shouldContainExactlyInAnyOrder listOf(cleanCode)
            courseRepository.findByTitleContaining("JPA") shouldBe emptyList()
        }

        test("findByInstructorId") {
            val instructor = prepareActiveInstructor()
            val instructor2 = prepareActiveInstructor()
            val course = prepareCourse(instructor, "Title")
            val course2 = prepareCourse(instructor2, "Title2")

            courseRepository.findByInstructorId(requireNotNull(instructor.id)) shouldHaveSingleElement course
            courseRepository.findByInstructorId(requireNotNull(instructor2.id)) shouldHaveSingleElement course2
        }

        test("findByInstructorIdAndTitle") {
            val instructor = prepareActiveInstructor()
            val course = prepareCourse(instructor, "Title")

            courseRepository.findByInstructorIdAndTitle(requireNotNull(instructor.id), "Title") shouldBe course
            courseRepository.findByInstructorIdAndTitle(requireNotNull(instructor.id), "No Such") shouldBe null
        }

        test("uniqueTitleAndInstructor") {
            val instructor = prepareActiveInstructor()
            prepareCourse(instructor, "Title")

            shouldThrow<DataIntegrityViolationException> {
                courseRepository.save(CourseFixture.createCourse(instructor, "Title"))
            }
        }
    }
}
