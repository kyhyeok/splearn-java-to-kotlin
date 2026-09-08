package kimspring.splearn.application.enrollment

import kimspring.splearn.application.course.usecase.CourseFinder
import kimspring.splearn.application.enrollment.command.EnrollCommand
import kimspring.splearn.application.enrollment.port.EnrollmentRepository
import kimspring.splearn.application.enrollment.usecase.Enroller
import kimspring.splearn.application.member.usecase.MemberFinder
import kimspring.splearn.domain.enrollment.DuplicateEnrollmentException
import kimspring.splearn.domain.enrollment.Enrollment
import kimspring.splearn.domain.shared.Clock
import kimspring.splearn.support.stereotype.ApplicationService

@ApplicationService
class EnrollmentModifyService(
    private val enrollmentRepository: EnrollmentRepository,
    private val memberFinder: MemberFinder,
    private val courseFinder: CourseFinder,
    private val clock: Clock,
) : Enroller {
    override fun enroll(command: EnrollCommand): Enrollment {
        val member = memberFinder.find(command.memberId)
        val course = courseFinder.find(command.courseId)
        checkDuplication(command.memberId, command.courseId)
        return enrollmentRepository.save(Enrollment.enroll(member, course, clock.now()))
    }

    override fun startStudying(enrollmentId: Long): Enrollment =
        enrollmentRepository.save(enrollmentRepository.getById(enrollmentId).startStudying())

    override fun complete(enrollmentId: Long): Enrollment =
        enrollmentRepository.save(enrollmentRepository.getById(enrollmentId).complete(clock.now()))

    private fun checkDuplication(
        memberId: Long,
        courseId: Long,
    ) {
        if (enrollmentRepository.findByMemberIdAndCourseId(memberId, courseId) != null) {
            throw DuplicateEnrollmentException("이미 수강 신청한 강의입니다: memberId=$memberId, courseId=$courseId")
        }
    }
}
