package kimspring.splearn.domain.member

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class DuplicateEmailException(
    message: String,
) : SplearnException(errorCode = ErrorCode.DUPLICATE_EMAIL, message = message)
