package kimspring.splearn.adapter.out.persistence

import org.springframework.data.repository.CrudRepository

interface SpringInstructorRepository : CrudRepository<InstructorJdbcEntity, Long> {
    fun findByMemberId(memberId: Long): InstructorJdbcEntity?
}
