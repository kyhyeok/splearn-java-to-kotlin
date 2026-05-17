package kimspring.splearn.domain.shared

abstract class SplearnException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
) : RuntimeException(message)
