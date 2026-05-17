package kimspring.splearn.adapter.integration

import kimspring.splearn.application.member.port.EmailSender
import kimspring.splearn.domain.shared.Email
import org.springframework.context.annotation.Fallback
import org.springframework.stereotype.Component

@Component
@Fallback
class DummyEmailSender : EmailSender {
    override fun send(
        email: Email,
        subject: String,
        body: String,
    ) {
        println("DummyEmailSender.send email: $email")
    }
}
