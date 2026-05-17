package kimspring.splearn.domain.member

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class MemberNotFoundException(
    id: Long,
) : SplearnException(
        errorCode = ErrorCode.MEMBER_NOT_FOUND,
        message = "회원을 찾을 수 없습니다. id: $id",
    )
