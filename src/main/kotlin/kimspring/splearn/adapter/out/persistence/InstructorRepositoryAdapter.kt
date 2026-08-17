package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.application.instructor.port.InstructorRepository
import kimspring.splearn.domain.instructor.Instructor
import kimspring.splearn.domain.instructor.InstructorNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class InstructorRepositoryAdapter(
    private val springInstructorRepository: SpringInstructorRepository,
) : InstructorRepository {
    override fun save(instructor: Instructor): Instructor =
        springInstructorRepository.save(InstructorJdbcEntity.from(instructor)).toDomain()

    override fun findById(id: Long): Instructor? = springInstructorRepository.findByIdOrNull(id)?.toDomain()

    override fun getById(id: Long): Instructor = findById(id) ?: throw InstructorNotFoundException(id)

    override fun findByMemberId(memberId: Long): Instructor? =
        springInstructorRepository.findByMemberId(memberId)?.toDomain()
}
