package kimspring.splearn.domain.curriculum

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class CurriculumNotFoundException(
    message: String = ErrorCode.CURRICULUM_NOT_FOUND.message,
) : SplearnException(errorCode = ErrorCode.CURRICULUM_NOT_FOUND, message = message)
