package kimspring.splearn.domain.enrollment

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class InvalidEnrollmentStateException(
    message: String = ErrorCode.INVALID_ENROLLMENT_STATE.message,
) : SplearnException(errorCode = ErrorCode.INVALID_ENROLLMENT_STATE, message = message)
