package kimspring.splearn.application.course.usecase

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import kimspring.splearn.application.course.port.CourseRepository
import kimspring.splearn.domain.course.CourseFixture
import kimspring.splearn.domain.course.CourseValidationException
import kimspring.splearn.domain.curriculum.InvalidCurriculumException
import kimspring.splearn.support.test.BaseApplicationServiceTest
import org.springframework.beans.factory.annotation.Autowired

class CourseValidatorTest : BaseApplicationServiceTest() {
    @Autowired
    private lateinit var courseValidator: CourseValidator

    @Autowired
    private lateinit var courseRepository: CourseRepository

    init {
        test("titleDuplicationForCreate") {
            val instructor1 = prepareInstructor()
            val instructor2 = prepareInstructor()
            courseRepository.save(CourseFixture.createCourse(instructor1, "Clean Spring"))
            courseRepository.save(CourseFixture.createCourse(instructor2, "Clean Code"))

            // instructor1, 중복되지 않는 제목 - OK
            courseValidator.validateForCreate(
                instructor1,
                CourseFixture.createCreateCourseCommand(requireNotNull(instructor1.id), "Spring 7"),
            )

            // instructor1, 중복 제목 - FAIL
            val e =
                shouldThrow<CourseValidationException> {
                    courseValidator.validateForCreate(
                        instructor1,
                        CourseFixture.createCreateCourseCommand(requireNotNull(instructor1.id), "Clean Spring"),
                    )
                }
            e.errors shouldHaveSize 1

            // instructor2, 1과 중복되는 제목 - OK
            courseValidator.validateForCreate(
                instructor2,
                CourseFixture.createCreateCourseCommand(requireNotNull(instructor2.id), "Clean Spring"),
            )
        }

        test("titleDuplicationForUpdate") {
            val instructor = prepareInstructor()
            val course1 = courseRepository.save(CourseFixture.createCourse(instructor, "Clean Spring"))
            val course2 = courseRepository.save(CourseFixture.createCourse(instructor, "Clean Code"))

            // title 변경 없이 update - OK
            courseValidator.validateForUpdate(course1, CourseFixture.createUpdateCourseInfoCommand(course1.title))

            // title 변경하는데 중복 발생 - FAIL
            val e =
                shouldThrow<CourseValidationException> {
                    courseValidator.validateForUpdate(
                        course1,
                        CourseFixture.createUpdateCourseInfoCommand(course2.title),
                    )
                }
            e.errors shouldHaveSize 1
        }

        test("validateForReview") {
            val course = prepareCourseWithCurriculum()

            shouldNotThrowAny { courseValidator.validateForReview(course) }
        }

        test("submitForReviewFailInvalidCurriculum") {
            val course = prepareCourse()
            val curriculum = prepareCurriculumSectionsAndLessons(course)
            // S3 의 유일한 수업을 지워 빈 섹션을 만든다
            curriculumCoordinator.removeLesson(requireNotNull(curriculum.id), 2, 0)

            shouldThrow<InvalidCurriculumException> { coursePublisher.submitForReview(requireNotNull(course.id)) }
        }

        test("validateForPublish") {
            val course = prepareCourseWithCurriculum()

            shouldNotThrowAny { courseValidator.validateForPublish(course) }
        }

        test("publishFailInvalidCurriculum") {
            val course = prepareCourse()
            val courseId = requireNotNull(course.id)
            val curriculum = prepareCurriculumSectionsAndLessons(course)
            coursePublisher.submitForReview(courseId)
            curriculumCoordinator.removeLesson(requireNotNull(curriculum.id), 2, 0)

            shouldThrow<InvalidCurriculumException> { coursePublisher.publish(courseId) }
        }
    }
}
