package kimspring.splearn.domain.member

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class InvalidMemberStateException(
    message: String = ErrorCode.INVALID_MEMBER_STATE.message,
) : SplearnException(errorCode = ErrorCode.INVALID_MEMBER_STATE, message = message)
