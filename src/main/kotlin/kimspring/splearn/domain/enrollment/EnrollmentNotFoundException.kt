package kimspring.splearn.domain.enrollment

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class EnrollmentNotFoundException(
    id: Long,
) : SplearnException(
        errorCode = ErrorCode.ENROLLMENT_NOT_FOUND,
        message = "수강을 찾을 수 없습니다. id: $id",
    )
