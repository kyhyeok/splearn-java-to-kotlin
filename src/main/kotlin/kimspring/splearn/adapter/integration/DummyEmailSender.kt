package kimspring.splearn.adapter.integration

import io.github.oshai.kotlinlogging.KotlinLogging
import kimspring.splearn.application.member.port.EmailSender
import kimspring.splearn.domain.shared.Email
import org.springframework.context.annotation.Fallback
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
@Fallback
class DummyEmailSender : EmailSender {
    override fun send(
        email: Email,
        subject: String,
        body: String,
    ) {
        log.info { "DummyEmailSender.send email: $email" }
    }
}
