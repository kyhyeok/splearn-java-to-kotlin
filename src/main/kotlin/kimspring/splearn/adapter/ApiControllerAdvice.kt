package kimspring.splearn.adapter

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import kimspring.splearn.adapter.webapi.dto.ErrorResponse
import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

private val log = KotlinLogging.logger {}

@ControllerAdvice
class ApiControllerAdvice : ResponseEntityExceptionHandler() {
    @ExceptionHandler(SplearnException::class)
    fun handleSplearnException(e: SplearnException): ResponseEntity<ErrorResponse> {
        if (e.errorCode.status >= 500) {
            log.error(e) { "${e.errorCode.code}: ${e.message}" }
        } else {
            log.warn { "${e.errorCode.code}: ${e.message}" }
        }
        return ResponseEntity.status(e.errorCode.status).body(ErrorResponse.of(e))
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> {
        // ex.message 는 거부된 입력값(비밀번호 포함)을 담으므로 필드명과 제약 코드만 남긴다
        log.warn { "유효성 검사 실패: ${ex.bindingResult.fieldErrors.map { "${it.field}:${it.code}" }}" }
        val fields =
            ex.bindingResult.fieldErrors.map { fieldError ->
                ErrorResponse.FieldError(
                    field = fieldError.field,
                    message = fieldError.defaultMessage ?: "유효하지 않은 값입니다.",
                )
            }
        return ResponseEntity(ErrorResponse.of(ErrorCode.INVALID_INPUT, fields), HttpStatus.BAD_REQUEST)
    }

    override fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> {
        log.warn { "지원하지 않는 HTTP 메서드: ${ex.method}" }
        return ResponseEntity(ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED), HttpStatus.METHOD_NOT_ALLOWED)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException): ResponseEntity<ErrorResponse> {
        log.warn { "접근 거부: ${e.message}" }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.of(ErrorCode.FORBIDDEN))
    }

    // 도메인 require 가 거절한 잘못된 입력. Command 검증이 먼저 막는 것이 원칙이고 이 핸들러는 안전망이다.
    // 내부 불변식 위반(requireNotNull 등)도 같은 예외 타입이라 400 으로 보이는 대가가 있다
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        log.warn { "잘못된 입력: ${e.message}" }
        return ResponseEntity.badRequest().body(ErrorResponse.of(ErrorCode.INVALID_INPUT))
    }

    // 사전 중복 검사를 동시에 통과한 요청이 유니크 제약에 걸린 경우. 데이터는 보호됐으므로 500 이 아니라 409 다.
    // DB 메시지에는 충돌한 값(이메일 등)이 실리므로 로그에 남기지 않는다
    @ExceptionHandler(DuplicateKeyException::class)
    fun handleDuplicateKey(): ResponseEntity<ErrorResponse> {
        log.warn { "유니크 제약 충돌로 요청을 거절했습니다" }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of(ErrorCode.DUPLICATE_RESOURCE))
    }

    @ExceptionHandler(OptimisticLockingFailureException::class)
    fun handleOptimisticLocking(e: OptimisticLockingFailureException): ResponseEntity<ErrorResponse> {
        log.warn { "동시 수정 충돌: ${e.message}" }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of(ErrorCode.CONCURRENT_MODIFICATION))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(e: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        log.warn { "제약 조건 위반: ${e.message}" }
        val fields =
            e.constraintViolations.map { violation ->
                ErrorResponse.FieldError(
                    field = violation.propertyPath.toString().substringAfterLast("."),
                    message = violation.message,
                )
            }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(ErrorCode.INVALID_INPUT, fields))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error(e) { "서버 오류 발생: ${e.message}" }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR))
    }
}
