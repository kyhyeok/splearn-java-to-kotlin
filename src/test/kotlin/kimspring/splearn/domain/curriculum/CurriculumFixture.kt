package kimspring.splearn.domain.curriculum

import kimspring.splearn.domain.course.Course
import kimspring.splearn.domain.course.CourseFixture
import kimspring.splearn.domain.member.MemberFixture

object CurriculumFixture {
    fun createCurriculum(course: Course = createSavedCourse()): Curriculum = Curriculum.create(course)

    private fun createSavedCourse(): Course = CourseFixture.createCourse().copy(id = MemberFixture.randomId())
}
