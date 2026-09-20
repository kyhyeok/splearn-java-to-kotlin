package kimspring.splearn.domain.curriculum

// section.title, lesson.title 컬럼 길이(VARCHAR(200))와 맞춘다. 초과 입력을 DB 예외(500) 대신 도메인에서 거절한다
const val CURRICULUM_TITLE_MAX_LENGTH = 200

data class Lesson(
    val id: Long? = null,
    val title: String,
) {
    init {
        require(title.length <= CURRICULUM_TITLE_MAX_LENGTH) { "수업 제목은 ${CURRICULUM_TITLE_MAX_LENGTH}자를 넘을 수 없습니다" }
    }

    fun updateTitle(title: String): Lesson = copy(title = title)

    companion object {
        fun create(title: String): Lesson = Lesson(title = title)
    }
}
