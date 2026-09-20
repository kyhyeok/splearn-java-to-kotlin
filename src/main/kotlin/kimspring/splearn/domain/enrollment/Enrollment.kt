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
    // 낙관적 락 버전. 같은 상태를 읽은 두 전이가 모두 성공해 나중 것이 앞선 결과를 덮는 것을 막는다
    val version: Long? = null,
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
