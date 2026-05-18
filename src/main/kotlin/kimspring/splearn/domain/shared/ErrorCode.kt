package kimspring.splearn.domain.shared

enum class ErrorCode(
    val status: Int,
    val code: String,
    val message: String,
) {
    INTERNAL_ERROR(500, "C001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT(400, "C002", "입력값이 올바르지 않습니다."),
    FORBIDDEN(403, "C003", "접근 권한이 없습니다."),
    METHOD_NOT_ALLOWED(405, "C004", "지원하지 않는 HTTP 메서드입니다."),
    GATEWAY_TIMEOUT(504, "C005", "외부 서비스 응답 시간이 초과되었습니다."),
    MEMBER_NOT_FOUND(404, "M001", "회원을 찾을 수 없습니다."),
    DUPLICATE_EMAIL(409, "M002", "이미 사용중인 이메일입니다."),
    DUPLICATE_PROFILE(409, "M003", "이미 존재하는 프로필 주소입니다."),
    INVALID_MEMBER_STATE(400, "M004", "회원 상태가 올바르지 않습니다."),
}
