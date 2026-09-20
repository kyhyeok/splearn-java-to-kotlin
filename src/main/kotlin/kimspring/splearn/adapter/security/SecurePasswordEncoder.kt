package kimspring.splearn.adapter.security

import kimspring.splearn.domain.member.PasswordEncoder
import org.springframework.context.annotation.Fallback
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

// 테스트 구성이 빠른 PasswordEncoder 를 등록하면 그쪽이 우선한다. 파라미터명-빈이름 일치에 의존하지 않도록 명시한다
@Component
@Fallback
class SecurePasswordEncoder : PasswordEncoder {
    private val bCryptPasswordEncoder = BCryptPasswordEncoder()

    override fun encode(password: String): String = bCryptPasswordEncoder.encode(password)!!

    override fun matches(
        password: String,
        passwordHash: String,
    ): Boolean = bCryptPasswordEncoder.matches(password, passwordHash)
}
