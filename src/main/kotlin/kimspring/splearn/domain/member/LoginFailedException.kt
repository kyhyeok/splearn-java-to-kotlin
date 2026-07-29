package kimspring.splearn.domain.member

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

// 실패 원인(이메일 없음·상태·비밀번호)을 메시지로 구분하지 않는다 — 계정 존재 여부가 노출된다
class LoginFailedException : SplearnException(errorCode = ErrorCode.LOGIN_FAILED)
