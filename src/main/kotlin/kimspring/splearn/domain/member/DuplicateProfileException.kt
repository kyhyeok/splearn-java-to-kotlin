package kimspring.splearn.domain.member

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class DuplicateProfileException(
    message: String,
) : SplearnException(errorCode = ErrorCode.DUPLICATE_PROFILE, message = message)
