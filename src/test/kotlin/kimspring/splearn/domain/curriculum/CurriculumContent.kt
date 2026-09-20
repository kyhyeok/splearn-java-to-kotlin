package kimspring.splearn.domain.curriculum

/**
 * 커리큘럼의 섹션·수업 구성을 제목만으로 비교하기 위한 테스트 전용 표현.
 * `section("S0", lesson("L0"))` 형태로 기대 구성을 기술한다
 */
data class SectionContent(
    val title: String,
    val lessons: List<LessonContent>,
) {
    companion object {
        fun from(curriculum: Curriculum): List<SectionContent> =
            curriculum.sections.map { section ->
                SectionContent(section.title, section.lessons.map { LessonContent(it.title) })
            }
    }
}

data class LessonContent(
    val title: String,
)

fun section(
    title: String,
    vararg lessons: LessonContent,
): SectionContent = SectionContent(title, lessons.toList())

fun lesson(title: String): LessonContent = LessonContent(title)
