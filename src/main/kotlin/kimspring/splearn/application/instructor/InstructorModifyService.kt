package kimspring.splearn.application.instructor

import kimspring.splearn.application.instructor.port.InstructorRepository
import kimspring.splearn.application.instructor.usecase.InstructorApplication
import kimspring.splearn.application.member.usecase.MemberFinder
import kimspring.splearn.domain.instructor.DuplicateInstructorApplicationException
import kimspring.splearn.domain.instructor.Instructor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Transactional
@Validated
class InstructorModifyService(
    private val instructorRepository: InstructorRepository,
    private val memberFinder: MemberFinder,
) : InstructorApplication {
    override fun apply(memberId: Long): Instructor {
        val member = memberFinder.find(memberId)
        checkDuplicateApplication(memberId)
        return instructorRepository.save(Instructor.apply(member))
    }

    override fun approve(instructorId: Long): Instructor {
        val instructor = instructorRepository.getById(instructorId)
        return instructorRepository.save(instructor.approve())
    }

    override fun reject(instructorId: Long): Instructor {
        val instructor = instructorRepository.getById(instructorId)
        return instructorRepository.save(instructor.reject())
    }

    private fun checkDuplicateApplication(memberId: Long) {
        if (instructorRepository.findByMemberId(memberId) != null) {
            throw DuplicateInstructorApplicationException("회원은 중복해서 강사 신청을 할 수 없습니다: memberId=$memberId")
        }
    }
}
