package kimspring.splearn.application.instructor.usecase

import kimspring.splearn.domain.instructor.Instructor

/**
 * 강사를 조회한다
 */
interface InstructorFinder {
    fun get(instructorId: Long): Instructor

    fun findByMember(memberId: Long): Instructor?
}
