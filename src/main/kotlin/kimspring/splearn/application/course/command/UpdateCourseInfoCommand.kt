package kimspring.splearn.application.course.command

import jakarta.validation.constraints.Size

data class UpdateCourseInfoCommand(
    @field:Size(min = 2, max = 100) val title: String,
    @field:Size(max = 500) val description: String?,
)
