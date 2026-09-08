package kimspring.splearn.application.course.usecase

import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.course.CourseStatus
import kimspring.splearn.support.test.BaseApplicationServiceTest

class CoursePublisherTest : BaseApplicationServiceTest() {
    init {
        test("submitForReview") {
            val courseId = requireNotNull(prepareCourse().id)

            coursePublisher.submitForReview(courseId).status shouldBe CourseStatus.IN_REVIEW
        }

        test("publish") {
            val courseId = requireNotNull(prepareCourse().id)
            coursePublisher.submitForReview(courseId)

            coursePublisher.publish(courseId).status shouldBe CourseStatus.PUBLISHED
        }

        test("archive") {
            val courseId = requireNotNull(prepareCourse().id)
            coursePublisher.submitForReview(courseId)
            coursePublisher.publish(courseId)

            coursePublisher.archive(courseId).status shouldBe CourseStatus.ARCHIVED
        }
    }
}
