package kimspring.splearn.application.instructor

import kimspring.splearn.application.instructor.port.InstructorRepository
import kimspring.splearn.application.instructor.usecase.InstructorFinder
import kimspring.splearn.domain.instructor.Instructor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Transactional(readOnly = true)
@Validated
class InstructorQueryService(
    private val instructorRepository: InstructorRepository,
) : InstructorFinder {
    override fun find(instructorId: Long): Instructor = instructorRepository.getById(instructorId)

    override fun findByMember(memberId: Long): Instructor? = instructorRepository.findByMemberId(memberId)
}
