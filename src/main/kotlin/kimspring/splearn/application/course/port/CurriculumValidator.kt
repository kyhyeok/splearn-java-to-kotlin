package kimspring.splearn.application.course.port

/**
 * 강의를 검수 신청·공개하기 전에 커리큘럼 구성을 검증한다.
 * 구성이 올바르지 않으면 InvalidCurriculumException 을 던진다
 */
interface CurriculumValidator {
    fun validate(courseId: Long)
}
