package kimspring.splearn.application.instructor.port

import kimspring.splearn.domain.instructor.Instructor

interface InstructorRepository {
    fun save(instructor: Instructor): Instructor

    fun findById(id: Long): Instructor?

    fun getById(id: Long): Instructor

    fun findByMemberId(memberId: Long): Instructor?
}
