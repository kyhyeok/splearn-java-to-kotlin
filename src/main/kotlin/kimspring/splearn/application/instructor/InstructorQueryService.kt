package kimspring.splearn.application.instructor

import kimspring.splearn.application.instructor.port.InstructorRepository
import kimspring.splearn.application.instructor.usecase.InstructorFinder
import kimspring.splearn.domain.instructor.Instructor
import kimspring.splearn.support.stereotype.QueryApplicationService

@QueryApplicationService
class InstructorQueryService(
    private val instructorRepository: InstructorRepository,
) : InstructorFinder {
    override fun find(instructorId: Long): Instructor = instructorRepository.getById(instructorId)

    override fun findByMember(memberId: Long): Instructor? = instructorRepository.findByMemberId(memberId)
}
