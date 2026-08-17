package kimspring.splearn.application.instructor.usecase

import kimspring.splearn.domain.instructor.Instructor

/**
 * 강사 신청과 신청 처리
 */
interface InstructorApplication {
    fun apply(memberId: Long): Instructor

    fun approve(instructorId: Long): Instructor

    fun reject(instructorId: Long): Instructor
}
