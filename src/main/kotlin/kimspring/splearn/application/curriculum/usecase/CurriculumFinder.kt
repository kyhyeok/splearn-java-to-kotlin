package kimspring.splearn.application.curriculum.usecase

import kimspring.splearn.domain.curriculum.Curriculum
import kimspring.splearn.domain.curriculum.Lesson

/**
 * 커리큘럼과 수업 순서를 조회한다
 */
interface CurriculumFinder {
    fun get(curriculumId: Long): Curriculum

    fun getByCourse(courseId: Long): Curriculum

    fun firstLesson(curriculumId: Long): Lesson?

    fun nextLesson(
        curriculumId: Long,
        lessonId: Long,
    ): Lesson?
}
