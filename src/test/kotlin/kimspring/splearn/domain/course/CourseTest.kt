package kimspring.splearn.domain.course

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.instructor.InstructorFixture
import kimspring.splearn.domain.instructor.InvalidInstructorStateException
import java.time.LocalDateTime

class CourseTest : FunSpec() {
    private val now = LocalDateTime.of(2024, 1, 1, 0, 0)
    private lateinit var course: Course

    init {
        beforeEach {
            course = CourseFixture.createCourse(description = "Description")
        }

        test("create") {
            val instructor = InstructorFixture.createActiveInstructor()

            val created = Course.create(instructor, "Clean Spring 2", "Description", now)

            created.instructorId shouldBe instructor.id
            created.title shouldBe "Clean Spring 2"
            created.status shouldBe CourseStatus.DRAFT
            created.detail.description shouldBe "Description"
            created.detail.createdAt shouldBe now
        }

        test("createFailNotActiveInstructor") {
            val instructor = InstructorFixture.createInstructor()

            shouldThrow<InvalidInstructorStateException> { Course.create(instructor, "title", null, now) }
        }

        test("submitForReview") {
            val inReview = course.submitForReview()

            inReview.status shouldBe CourseStatus.IN_REVIEW

            shouldThrow<InvalidCourseStateException> { inReview.submitForReview() }
        }

        test("submitForReviewFail") {
            val noDescription = CourseFixture.createCourse()

            shouldThrow<InvalidCourseStateException> { noDescription.submitForReview() }
        }

        test("publish") {
            val published = course.submitForReview().publish(now)

            published.status shouldBe CourseStatus.PUBLISHED
            published.detail.publishedAt.shouldNotBeNull()

            shouldThrow<InvalidCourseStateException> { published.publish(now) }
        }

        test("archive") {
            val archived = course.submitForReview().publish(now).archive(now)

            archived.status shouldBe CourseStatus.ARCHIVED
            archived.detail.archivedAt.shouldNotBeNull()

            shouldThrow<InvalidCourseStateException> { archived.archive(now) }
        }

        test("isPublished") {
            course.isPublished() shouldBe false

            course.submitForReview().publish(now).isPublished() shouldBe true
        }

        test("ensurePublished") {
            shouldThrow<InvalidCourseStateException> { course.ensurePublished() }

            course.submitForReview().publish(now).ensurePublished()
        }

        test("updateInfo") {
            val updated = course.updateInfo("Clean Spring 3", "Updated Description")

            updated.title shouldBe "Clean Spring 3"
            updated.detail.description shouldBe "Updated Description"
        }
    }
}
