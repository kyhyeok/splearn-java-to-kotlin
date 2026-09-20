package kimspring.splearn.application.curriculum.usecase

import kimspring.splearn.application.course.port.CurriculumCreator
import kimspring.splearn.application.course.port.CurriculumValidator
import kimspring.splearn.domain.curriculum.Curriculum

/**
 * 커리큘럼의 섹션과 수업 구성을 편집한다.
 * 강의 슬라이스가 요구하는 CurriculumCreator·CurriculumValidator 포트도 함께 제공한다
 */
interface CurriculumCoordinator :
    CurriculumCreator,
    CurriculumValidator {
    fun addSection(
        curriculumId: Long,
        title: String,
    ): Curriculum

    fun addSection(
        curriculumId: Long,
        sectionIndex: Int,
        title: String,
    ): Curriculum

    fun addLesson(
        curriculumId: Long,
        sectionIndex: Int,
        title: String,
    ): Curriculum

    fun updateSectionTitle(
        curriculumId: Long,
        sectionIndex: Int,
        title: String,
    ): Curriculum

    fun updateLessonTitle(
        curriculumId: Long,
        sectionIndex: Int,
        lessonIndex: Int,
        title: String,
    ): Curriculum

    fun removeSection(
        curriculumId: Long,
        sectionIndex: Int,
    ): Curriculum

    fun removeLesson(
        curriculumId: Long,
        sectionIndex: Int,
        lessonIndex: Int,
    ): Curriculum

    fun moveLesson(
        curriculumId: Long,
        fromSectionIndex: Int,
        fromLessonIndex: Int,
        toSectionIndex: Int,
        toLessonIndex: Int,
    ): Curriculum
}
