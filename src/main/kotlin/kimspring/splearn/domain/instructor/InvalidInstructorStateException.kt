package kimspring.splearn.domain.instructor

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class InvalidInstructorStateException(
    message: String = ErrorCode.INVALID_INSTRUCTOR_STATE.message,
) : SplearnException(errorCode = ErrorCode.INVALID_INSTRUCTOR_STATE, message = message)
