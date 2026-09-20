package kimspring.splearn.adapter.integration

import io.github.oshai.kotlinlogging.KotlinLogging
import kimspring.splearn.application.member.port.EmailSender
import kimspring.splearn.domain.shared.Email
import org.springframework.context.annotation.Fallback
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

private val log = KotlinLogging.logger {}

@Component
@Fallback
class DummyEmailSender : EmailSender {
    // 트랜잭션 안에서 호출되면 커밋 후에 발송한다. 외부 지연이 커넥션·행 락을 붙잡거나
    // 발송 실패가 저장을 롤백시키지 않게 한다. 실제 발송기로 교체할 때도 같은 규칙을 지킨다
    override fun send(
        email: Email,
        subject: String,
        body: String,
    ) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                object : TransactionSynchronization {
                    override fun afterCommit() = doSend(subject)
                },
            )
        } else {
            doSend(subject)
        }
    }

    // 수신 주소는 개인정보라 로그에 남기지 않는다
    private fun doSend(subject: String) {
        log.info { "DummyEmailSender.send subject: $subject" }
    }
}
