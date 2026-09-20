package kimspring.splearn.application.member.command

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kimspring.splearn.domain.member.MemberRegisterInfo
import kimspring.splearn.domain.shared.Email
import kimspring.splearn.support.validation.BCRYPT_MAX_PASSWORD_BYTES
import kimspring.splearn.support.validation.MaxByteLength

data class RegisterMemberCommand(
    @field:Pattern(regexp = Email.EMAIL_REGEX) val email: String,
    @field:Size(min = 5, max = 20) val nickname: String,
    @field:Size(min = 8, max = BCRYPT_MAX_PASSWORD_BYTES)
    @field:MaxByteLength(BCRYPT_MAX_PASSWORD_BYTES)
    val password: String,
) {
    fun toInfo(): MemberRegisterInfo = MemberRegisterInfo(email, nickname, password)
}
