package kimspring.splearn.domain.instructor

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class InstructorNotFoundException(
    id: Long,
) : SplearnException(
        errorCode = ErrorCode.INSTRUCTOR_NOT_FOUND,
        message = "강사를 찾을 수 없습니다. id: $id",
    )
