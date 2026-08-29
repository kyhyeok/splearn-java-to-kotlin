package kimspring.splearn.domain.course

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class CourseValidationException(
    val errors: List<String>,
) : SplearnException(
        errorCode = ErrorCode.COURSE_VALIDATION_FAILED,
        message = errors.joinToString("; "),
    )
