package kimspring.splearn.application.curriculum.port

import kimspring.splearn.domain.curriculum.Curriculum

interface CurriculumRepository {
    fun save(curriculum: Curriculum): Curriculum

    fun findById(id: Long): Curriculum?

    fun getById(id: Long): Curriculum

    fun findByCourseId(courseId: Long): Curriculum?

    fun getByCourseId(courseId: Long): Curriculum
}
