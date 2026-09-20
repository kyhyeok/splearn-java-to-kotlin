package kimspring.splearn.domain.curriculum

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class InvalidCurriculumException(
    message: String = ErrorCode.INVALID_CURRICULUM.message,
) : SplearnException(errorCode = ErrorCode.INVALID_CURRICULUM, message = message)
