package kimspring.learningtest.instancio

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UserRegisterRequest(
    @field:Email val email: String,
    @field:Size(min = 5, max = 20) val nickname: String,
    @field:Size(min = 8, max = 100) val password: String,
)
