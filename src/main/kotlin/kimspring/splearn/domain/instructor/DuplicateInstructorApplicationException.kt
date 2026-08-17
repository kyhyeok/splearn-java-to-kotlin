package kimspring.splearn.domain.instructor

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class DuplicateInstructorApplicationException(
    message: String = ErrorCode.DUPLICATE_INSTRUCTOR_APPLICATION.message,
) : SplearnException(errorCode = ErrorCode.DUPLICATE_INSTRUCTOR_APPLICATION, message = message)
