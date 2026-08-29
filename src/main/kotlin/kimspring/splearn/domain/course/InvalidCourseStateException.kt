package kimspring.splearn.domain.course

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class InvalidCourseStateException(
    message: String = ErrorCode.INVALID_COURSE_STATE.message,
) : SplearnException(errorCode = ErrorCode.INVALID_COURSE_STATE, message = message)
