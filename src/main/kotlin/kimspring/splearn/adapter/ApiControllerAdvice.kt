package kimspring.splearn.adapter

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import kimspring.splearn.adapter.webapi.dto.ErrorResponse
import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
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
        log.warn { "유효성 검사 실패: ${ex.message}" }
        return ResponseEntity(ErrorResponse.of(ErrorCode.INVALID_INPUT), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(e: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        log.warn { "제약 조건 위반: ${e.message}" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(ErrorCode.INVALID_INPUT))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error(e) { "서버 오류 발생: ${e.message}" }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR))
    }
}
