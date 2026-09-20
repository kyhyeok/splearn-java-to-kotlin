package kimspring.splearn.domain.curriculum

import kimspring.splearn.domain.course.Course
import kimspring.splearn.domain.course.CourseFixture

object CurriculumFixture {
    fun createCurriculum(course: Course = CourseFixture.createCourse().copy(id = 1L)): Curriculum =
        Curriculum.create(course)
}
