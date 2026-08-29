package kimspring.splearn.domain.course

import java.time.LocalDateTime

data class CourseDetail(
    val description: String? = null,
    val createdAt: LocalDateTime,
    val publishedAt: LocalDateTime? = null,
    val archivedAt: LocalDateTime? = null,
) {
    fun recordPublication(now: LocalDateTime): CourseDetail {
        check(publishedAt == null) { "이미 publishedAt은 설정되었습니다" }
        return copy(publishedAt = now)
    }

    fun recordArchiving(now: LocalDateTime): CourseDetail {
        check(archivedAt == null) { "이미 archivedAt은 설정되었습니다" }
        return copy(archivedAt = now)
    }

    fun updateInfo(description: String?): CourseDetail = copy(description = description)

    companion object {
        fun create(
            description: String?,
            now: LocalDateTime,
        ): CourseDetail = CourseDetail(description = description, createdAt = now)
    }
}
