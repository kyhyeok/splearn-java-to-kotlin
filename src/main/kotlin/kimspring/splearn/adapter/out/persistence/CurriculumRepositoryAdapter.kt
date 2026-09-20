package kimspring.splearn.adapter.out.persistence

import kimspring.splearn.application.curriculum.port.CurriculumRepository
import kimspring.splearn.domain.curriculum.Curriculum
import kimspring.splearn.domain.curriculum.CurriculumNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class CurriculumRepositoryAdapter(
    private val springCurriculumRepository: SpringCurriculumRepository,
) : CurriculumRepository {
    override fun save(curriculum: Curriculum): Curriculum =
        springCurriculumRepository.save(CurriculumJdbcEntity.from(curriculum)).toDomain()

    override fun findById(id: Long): Curriculum? = springCurriculumRepository.findByIdOrNull(id)?.toDomain()

    override fun getById(id: Long): Curriculum =
        findById(id) ?: throw CurriculumNotFoundException("커리큘럼을 찾을 수 없습니다. id: $id")

    override fun findByCourseId(courseId: Long): Curriculum? =
        springCurriculumRepository.findByCourseId(courseId)?.toDomain()

    override fun getByCourseId(courseId: Long): Curriculum =
        findByCourseId(courseId) ?: throw CurriculumNotFoundException("커리큘럼을 찾을 수 없습니다. courseId: $courseId")
}
