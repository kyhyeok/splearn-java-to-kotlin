package kimspring.splearn.support.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

// BCrypt 는 72바이트를 넘는 비밀번호를 거절한다. 문자 수(@Size)로는 다바이트 문자를 걸러낼 수 없다
const val BCRYPT_MAX_PASSWORD_BYTES = 72

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [MaxByteLengthValidator::class])
annotation class MaxByteLength(
    val max: Int,
    val message: String = "UTF-8 기준 {max}바이트를 넘을 수 없습니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class MaxByteLengthValidator : ConstraintValidator<MaxByteLength, String?> {
    private var max = 0

    override fun initialize(constraintAnnotation: MaxByteLength) {
        max = constraintAnnotation.max
    }

    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext?,
    ): Boolean = value == null || value.toByteArray(Charsets.UTF_8).size <= max
}
