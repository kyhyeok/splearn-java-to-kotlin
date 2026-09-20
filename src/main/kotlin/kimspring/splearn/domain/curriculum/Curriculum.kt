package kimspring.splearn.domain.curriculum

import kimspring.splearn.domain.course.Course

data class Curriculum(
    val id: Long? = null,
    val courseId: Long,
    val sections: List<Section> = emptyList(),
    // 낙관적 락 버전. 편집이 위치 인덱스 기반이라 동시 저장이 조용히 덮어쓰는 것을 막는다
    val version: Long? = null,
) {
    fun addSection(title: String): Curriculum = addSection(sections.size, title)

    fun addSection(
        sectionIndex: Int,
        title: String,
    ): Curriculum = copy(sections = sections.toMutableList().apply { add(sectionIndex, Section.create(title)) })

    fun addLesson(
        sectionIndex: Int,
        title: String,
    ): Curriculum = updateSection(sectionIndex) { it.addLesson(title) }

    fun updateSectionTitle(
        sectionIndex: Int,
        title: String,
    ): Curriculum = updateSection(sectionIndex) { it.updateTitle(title) }

    fun updateLessonTitle(
        sectionIndex: Int,
        lessonIndex: Int,
        title: String,
    ): Curriculum = updateSection(sectionIndex) { it.updateLessonTitle(lessonIndex, title) }

    fun removeLesson(
        sectionIndex: Int,
        lessonIndex: Int,
    ): Curriculum = updateSection(sectionIndex) { it.removeLesson(lessonIndex) }

    fun removeSection(sectionIndex: Int): Curriculum {
        if (sections.size <= 1) throw InvalidCurriculumException("마지막 남은 섹션은 삭제할 수 없습니다")
        val removed = sections[sectionIndex]
        val remaining = sections.toMutableList().apply { removeAt(sectionIndex) }
        // 삭제한 섹션의 수업은 이전 섹션 끝으로, 첫 섹션이었다면 다음 섹션 맨 앞으로 옮긴다
        val targetIndex = if (sectionIndex == 0) 0 else sectionIndex - 1
        val insertIndex = if (sectionIndex == 0) 0 else remaining[targetIndex].lessons.size
        remaining[targetIndex] = remaining[targetIndex].addLessons(insertIndex, removed.lessons)
        return copy(sections = remaining)
    }

    fun moveLesson(
        fromSectionIndex: Int,
        fromLessonIndex: Int,
        toSectionIndex: Int,
        toLessonIndex: Int,
    ): Curriculum {
        val lesson = sections[fromSectionIndex].lessons[fromLessonIndex]
        val moved = sections.toMutableList()
        moved[fromSectionIndex] = moved[fromSectionIndex].removeLesson(fromLessonIndex)
        moved[toSectionIndex] = moved[toSectionIndex].addLesson(toLessonIndex, lesson)
        return copy(sections = moved)
    }

    fun allLessons(): List<Lesson> = sections.flatMap { it.lessons }

    fun validate() {
        if (sections.isEmpty()) throw InvalidCurriculumException("최소한 하나의 섹션이 필요합니다")
        if (sections.any { it.lessons.isEmpty() }) throw InvalidCurriculumException("수업이 없는 섹션은 허용되지 않습니다")
    }

    fun firstLesson(): Lesson? = allLessons().firstOrNull()

    fun nextLesson(lesson: Lesson): Lesson? {
        val lessons = allLessons()
        val index = lessons.indexOf(lesson)
        require(index >= 0) { "커리큘럼에 포함된 수업이 아닙니다" }
        return lessons.getOrNull(index + 1)
    }

    fun nextLesson(lessonId: Long): Lesson? =
        nextLesson(allLessons().firstOrNull { it.id == lessonId } ?: throw LessonNotFoundException(lessonId))

    private fun updateSection(
        sectionIndex: Int,
        change: (Section) -> Section,
    ): Curriculum = copy(sections = sections.toMutableList().apply { this[sectionIndex] = change(this[sectionIndex]) })

    companion object {
        fun create(course: Course): Curriculum =
            Curriculum(courseId = requireNotNull(course.id) { "저장되지 않은 강의에는 커리큘럼을 만들 수 없습니다." })
    }
}
