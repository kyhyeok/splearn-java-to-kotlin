package kimspring.splearn.domain.instructor

import kimspring.splearn.domain.member.InvalidMemberStateException
import kimspring.splearn.domain.member.Member

data class Instructor(
    val id: Long? = null,
    val memberId: Long,
    val status: InstructorStatus,
) {
    fun approve(): Instructor {
        if (status != InstructorStatus.PENDING) throw InvalidInstructorStateException("PENDING 상태가 아닙니다.")
        return copy(status = InstructorStatus.ACTIVE)
    }

    fun reject(): Instructor {
        if (status != InstructorStatus.PENDING) throw InvalidInstructorStateException("PENDING 상태가 아닙니다.")
        return copy(status = InstructorStatus.REJECTED)
    }

    fun isActive(): Boolean = status == InstructorStatus.ACTIVE

    fun ensureActive() {
        if (!isActive()) throw InvalidInstructorStateException("ACTIVE 상태가 아닙니다.")
    }

    companion object {
        fun apply(member: Member): Instructor {
            if (!member.isActive()) throw InvalidMemberStateException("등록 완료 상태가 아닌 회원은 강사 신청을 할 수 없습니다.")
            return Instructor(
                memberId = requireNotNull(member.id) { "저장되지 않은 회원은 강사 신청을 할 수 없습니다." },
                status = InstructorStatus.PENDING,
            )
        }
    }
}
