package kimspring.splearn

import kimspring.splearn.application.member.required.EmailSender
import kimspring.splearn.domain.member.MemberFixture
import kimspring.splearn.domain.member.PasswordEncoder
import kimspring.splearn.domain.shared.Email
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class SplearnTestConfiguration {
    @Bean
    fun emailSender(): EmailSender =
        object : EmailSender {
            override fun send(
                email: Email,
                subject: String,
                body: String,
            ) {
                println("Sending email: $email")
            }
        }

    @Bean
    fun passwordEncoder(): PasswordEncoder = MemberFixture.createPasswordEncoder()
}
