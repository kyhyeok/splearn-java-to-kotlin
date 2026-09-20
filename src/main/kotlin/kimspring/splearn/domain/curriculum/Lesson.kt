package kimspring.splearn.domain.curriculum

data class Lesson(
    val id: Long? = null,
    val title: String,
) {
    fun updateTitle(title: String): Lesson = copy(title = title)

    companion object {
        fun create(title: String): Lesson = Lesson(title = title)
    }
}
