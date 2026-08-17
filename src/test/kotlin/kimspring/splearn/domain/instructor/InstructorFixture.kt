package kimspring.splearn.domain.instructor

import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberFixture

object InstructorFixture {
    fun createInstructor(member: Member): Instructor = Instructor.apply(member)

    fun createInstructor(): Instructor = createInstructor(MemberFixture.createActiveMember())

    fun createActiveInstructor(): Instructor = createInstructor().approve()
}
