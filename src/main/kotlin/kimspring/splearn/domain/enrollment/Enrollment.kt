package kimspring.splearn.domain.enrollment

import kimspring.splearn.domain.course.Course
import kimspring.splearn.domain.member.Member
import java.time.LocalDateTime

data class Enrollment(
    val id: Long? = null,
    val memberId: Long,
    val courseId: Long,
    val status: EnrollmentStatus,
    val enrolledAt: LocalDateTime,
    val completedAt: LocalDateTime? = null,
) {
    fun startStudying(): Enrollment {
        if (status != EnrollmentStatus.ENROLLED) throw InvalidEnrollmentStateException("ENROLLED 상태가 아닙니다.")
        return copy(status = EnrollmentStatus.STUDYING)
    }

    fun complete(now: LocalDateTime): Enrollment {
        if (status != EnrollmentStatus.STUDYING) throw InvalidEnrollmentStateException("STUDYING 상태가 아닙니다.")
        return copy(status = EnrollmentStatus.COMPLETED, completedAt = now)
    }

    companion object {
        fun enroll(
            member: Member,
            course: Course,
            now: LocalDateTime,
        ): Enrollment {
            member.ensureActive()
            course.ensurePublished()
            return Enrollment(
                memberId = requireNotNull(member.id) { "저장되지 않은 회원은 수강 신청을 할 수 없습니다." },
                courseId = requireNotNull(course.id) { "저장되지 않은 강의는 수강 신청을 받을 수 없습니다." },
                status = EnrollmentStatus.ENROLLED,
                enrolledAt = now,
            )
        }
    }
}
