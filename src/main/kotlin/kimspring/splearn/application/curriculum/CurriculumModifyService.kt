package kimspring.splearn.application.curriculum

import kimspring.splearn.application.curriculum.port.CurriculumRepository
import kimspring.splearn.application.curriculum.usecase.CurriculumCoordinator
import kimspring.splearn.domain.course.Course
import kimspring.splearn.domain.curriculum.Curriculum
import kimspring.splearn.support.stereotype.ApplicationService

@ApplicationService
class CurriculumModifyService(
    private val curriculumRepository: CurriculumRepository,
) : CurriculumCoordinator {
    override fun createCurriculum(course: Course): Long =
        requireNotNull(curriculumRepository.save(Curriculum.create(course)).id)

    override fun addSection(
        curriculumId: Long,
        title: String,
    ): Curriculum = modify(curriculumId) { it.addSection(title) }

    override fun addSection(
        curriculumId: Long,
        sectionIndex: Int,
        title: String,
    ): Curriculum = modify(curriculumId) { it.addSection(sectionIndex, title) }

    override fun addLesson(
        curriculumId: Long,
        sectionIndex: Int,
        title: String,
    ): Curriculum = modify(curriculumId) { it.addLesson(sectionIndex, title) }

    override fun updateSectionTitle(
        curriculumId: Long,
        sectionIndex: Int,
        title: String,
    ): Curriculum = modify(curriculumId) { it.updateSectionTitle(sectionIndex, title) }

    override fun updateLessonTitle(
        curriculumId: Long,
        sectionIndex: Int,
        lessonIndex: Int,
        title: String,
    ): Curriculum = modify(curriculumId) { it.updateLessonTitle(sectionIndex, lessonIndex, title) }

    override fun removeSection(
        curriculumId: Long,
        sectionIndex: Int,
    ): Curriculum = modify(curriculumId) { it.removeSection(sectionIndex) }

    override fun removeLesson(
        curriculumId: Long,
        sectionIndex: Int,
        lessonIndex: Int,
    ): Curriculum = modify(curriculumId) { it.removeLesson(sectionIndex, lessonIndex) }

    override fun moveLesson(
        curriculumId: Long,
        fromSectionIndex: Int,
        fromLessonIndex: Int,
        toSectionIndex: Int,
        toLessonIndex: Int,
    ): Curriculum =
        modify(curriculumId) {
            it.moveLesson(fromSectionIndex, fromLessonIndex, toSectionIndex, toLessonIndex)
        }

    override fun validate(courseId: Long) {
        curriculumRepository.getByCourseId(courseId).validate()
    }

    private fun modify(
        curriculumId: Long,
        change: (Curriculum) -> Curriculum,
    ): Curriculum = curriculumRepository.save(change(curriculumRepository.getById(curriculumId)))
}
