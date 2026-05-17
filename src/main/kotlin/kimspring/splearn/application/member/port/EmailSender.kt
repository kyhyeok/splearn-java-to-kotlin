package kimspring.splearn.application.member.port

import kimspring.splearn.domain.shared.Email

/**
 * 이메일을 발송한다
 */
interface EmailSender {
    fun send(
        email: Email,
        subject: String,
        body: String,
    )
}
