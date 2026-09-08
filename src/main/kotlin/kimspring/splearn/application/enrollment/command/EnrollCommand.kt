package kimspring.splearn.application.enrollment.command

data class EnrollCommand(
    val memberId: Long,
    val courseId: Long,
)
