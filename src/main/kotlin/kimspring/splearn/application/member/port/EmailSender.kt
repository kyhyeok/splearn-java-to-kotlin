package kimspring.splearn.application.member.port

import kimspring.splearn.domain.shared.Email

/**
 * 이메일을 발송한다.
 * 구현체는 호출 시점의 트랜잭션이 커밋된 뒤에 실제 발송해야 한다 — 발송 지연·실패가 저장에 영향을 주지 않도록.
 */
interface EmailSender {
    fun send(
        email: Email,
        subject: String,
        body: String,
    )
}
