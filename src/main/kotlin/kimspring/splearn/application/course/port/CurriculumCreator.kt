package kimspring.splearn.application.course.port

import kimspring.splearn.domain.course.Course

/**
 * 강의를 만들 때 커리큘럼을 함께 만든다.
 * 강의 슬라이스가 요구하는 포트로, 구현은 커리큘럼 슬라이스가 담당한다 (DIP)
 */
interface CurriculumCreator {
    fun createCurriculum(course: Course): Long
}
