package kimspring.splearn.application.curriculum

import kimspring.splearn.application.curriculum.port.CurriculumRepository
import kimspring.splearn.application.curriculum.usecase.CurriculumFinder
import kimspring.splearn.domain.curriculum.Curriculum
import kimspring.splearn.domain.curriculum.Lesson
import kimspring.splearn.support.stereotype.QueryApplicationService

@QueryApplicationService
class CurriculumQueryService(
    private val curriculumRepository: CurriculumRepository,
) : CurriculumFinder {
    override fun find(curriculumId: Long): Curriculum = curriculumRepository.getById(curriculumId)

    override fun findByCourse(courseId: Long): Curriculum = curriculumRepository.getByCourseId(courseId)

    override fun firstLesson(curriculumId: Long): Lesson? = find(curriculumId).firstLesson()

    override fun nextLesson(
        curriculumId: Long,
        lessonId: Long,
    ): Lesson? = find(curriculumId).nextLesson(lessonId)
}
