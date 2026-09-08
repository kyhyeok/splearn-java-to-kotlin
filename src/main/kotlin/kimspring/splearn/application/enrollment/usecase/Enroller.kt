package kimspring.splearn.application.enrollment.usecase

import kimspring.splearn.application.enrollment.command.EnrollCommand
import kimspring.splearn.domain.enrollment.Enrollment

/**
 * 수강 신청과 수강 상태 관리
 */
interface Enroller {
    fun enroll(command: EnrollCommand): Enrollment

    fun startStudying(enrollmentId: Long): Enrollment

    fun complete(enrollmentId: Long): Enrollment
}
