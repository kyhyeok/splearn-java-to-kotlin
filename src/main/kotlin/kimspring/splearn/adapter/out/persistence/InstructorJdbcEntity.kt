package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.domain.instructor.Instructor
import kimspring.splearn.domain.instructor.InstructorStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("instructor")
data class InstructorJdbcEntity(
    @Id val id: Long? = null,
    val memberId: Long,
    val status: InstructorStatus,
) {
    fun toDomain(): Instructor =
        Instructor(
            id = id,
            memberId = memberId,
            status = status,
        )

    companion object {
        fun from(instructor: Instructor): InstructorJdbcEntity =
            InstructorJdbcEntity(
                id = instructor.id,
                memberId = instructor.memberId,
                status = instructor.status,
            )
    }
}
