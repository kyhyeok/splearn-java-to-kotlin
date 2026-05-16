package kimspring.splearn.adapter.security

import kimspring.splearn.domain.member.PasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class SecurePasswordEncoder : PasswordEncoder {
    private val bCryptPasswordEncoder = BCryptPasswordEncoder()

    override fun encode(password: String): String = bCryptPasswordEncoder.encode(password)!!

    override fun matches(
        password: String,
        passwordHash: String,
    ): Boolean = bCryptPasswordEncoder.matches(password, passwordHash)
}
