package kimspring.splearn.adapter.webapi.dto

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

data class ErrorResponse(
    val code: String,
    val message: String,
    val fields: List<FieldError>? = null,
) {
    data class FieldError(
        val field: String,
        val message: String,
    )

    companion object {
        fun of(e: SplearnException) = ErrorResponse(code = e.errorCode.code, message = e.message)

        fun of(errorCode: ErrorCode) = ErrorResponse(code = errorCode.code, message = errorCode.message)

        fun of(
            errorCode: ErrorCode,
            fields: List<FieldError>,
        ) = ErrorResponse(code = errorCode.code, message = errorCode.message, fields = fields)
    }
}
