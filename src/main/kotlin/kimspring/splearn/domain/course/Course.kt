package kimspring.splearn.domain.course

import kimspring.splearn.domain.instructor.Instructor
import java.time.LocalDateTime

data class Course(
    val id: Long? = null,
    val instructorId: Long,
    val title: String,
    val status: CourseStatus,
    val detail: CourseDetail,
) {
    fun submitForReview(): Course {
        if (status != CourseStatus.DRAFT) throw InvalidCourseStateException("DRAFT 상태가 아닙니다.")
        if (detail.description.isNullOrBlank()) throw InvalidCourseStateException("강의 소개가 등록되지 않았습니다.")
        return copy(status = CourseStatus.IN_REVIEW)
    }

    fun publish(now: LocalDateTime): Course {
        if (status != CourseStatus.IN_REVIEW) throw InvalidCourseStateException("IN_REVIEW 상태가 아닙니다.")
        return copy(status = CourseStatus.PUBLISHED, detail = detail.recordPublication(now))
    }

    fun archive(now: LocalDateTime): Course {
        if (status != CourseStatus.PUBLISHED) throw InvalidCourseStateException("PUBLISHED 상태가 아닙니다.")
        return copy(status = CourseStatus.ARCHIVED, detail = detail.recordArchiving(now))
    }

    fun isPublished(): Boolean = status == CourseStatus.PUBLISHED

    fun ensurePublished() {
        if (!isPublished()) throw InvalidCourseStateException("PUBLISHED 상태가 아닙니다.")
    }

    fun updateInfo(
        title: String,
        description: String?,
    ): Course = copy(title = title, detail = detail.updateInfo(description))

    companion object {
        fun create(
            instructor: Instructor,
            title: String,
            description: String?,
            now: LocalDateTime,
        ): Course {
            instructor.ensureActive()
            return Course(
                instructorId = requireNotNull(instructor.id) { "저장되지 않은 강사는 강의를 만들 수 없습니다." },
                title = title,
                status = CourseStatus.DRAFT,
                detail = CourseDetail.create(description, now),
            )
        }
    }
}
