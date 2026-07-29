package kimspring.splearn.application.member.command

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class LoginMemberCommand(
    @field:Email val email: String,
    @field:Size(min = 8, max = 100) val password: String,
)
