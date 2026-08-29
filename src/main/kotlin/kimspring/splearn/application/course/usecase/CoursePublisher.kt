package kimspring.splearn.application.course.usecase

import kimspring.splearn.domain.course.Course

/**
 * 강의 공개와 관련된 작업
 */
interface CoursePublisher {
    fun submitForReview(courseId: Long): Course

    fun publish(courseId: Long): Course

    fun archive(courseId: Long): Course
}
