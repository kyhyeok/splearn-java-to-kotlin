package kimspring.splearn.domain.curriculum

data class Section(
    val id: Long? = null,
    val title: String,
    val lessons: List<Lesson> = emptyList(),
) {
    init {
        require(title.length <= CURRICULUM_TITLE_MAX_LENGTH) { "섹션 제목은 ${CURRICULUM_TITLE_MAX_LENGTH}자를 넘을 수 없습니다" }
    }

    fun addLesson(title: String): Section = copy(lessons = lessons + Lesson.create(title))

    fun addLesson(
        lessonIndex: Int,
        lesson: Lesson,
    ): Section = addLessons(lessonIndex, listOf(lesson))

    fun addLessons(
        lessonIndex: Int,
        newLessons: List<Lesson>,
    ): Section = updateLessons { addAll(lessonIndex, newLessons) }

    fun updateTitle(title: String): Section = copy(title = title)

    fun updateLessonTitle(
        lessonIndex: Int,
        title: String,
    ): Section = updateLessons { this[lessonIndex] = this[lessonIndex].updateTitle(title) }

    fun removeLesson(lessonIndex: Int): Section = updateLessons { removeAt(lessonIndex) }

    private fun updateLessons(change: MutableList<Lesson>.() -> Unit): Section =
        copy(lessons = lessons.toMutableList().apply(change))

    companion object {
        fun create(title: String): Section = Section(title = title)
    }
}
