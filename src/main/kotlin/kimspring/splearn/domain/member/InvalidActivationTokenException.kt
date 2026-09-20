package kimspring.splearn.domain.member

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class InvalidActivationTokenException(
    message: String,
) : SplearnException(
        errorCode = ErrorCode.INVALID_ACTIVATION_TOKEN,
        message = message,
    )
