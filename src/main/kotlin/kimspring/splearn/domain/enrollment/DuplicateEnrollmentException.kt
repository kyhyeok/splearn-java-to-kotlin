package kimspring.splearn.domain.enrollment

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class DuplicateEnrollmentException(
    message: String = ErrorCode.DUPLICATE_ENROLLMENT.message,
) : SplearnException(errorCode = ErrorCode.DUPLICATE_ENROLLMENT, message = message)
