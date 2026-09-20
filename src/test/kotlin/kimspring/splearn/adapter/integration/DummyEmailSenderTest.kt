package kimspring.splearn.adapter.integration

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import kimspring.splearn.domain.shared.Email
import org.springframework.transaction.support.TransactionSynchronizationManager

class DummyEmailSenderTest :
    FunSpec({
        test("dummyEmailSender") {
            DummyEmailSender().send(Email("kim@splearn.app"), "subject", "body")
        }

        // 트랜잭션 동기화가 활성이면 즉시 보내지 않고 커밋 후 콜백으로 등록한다
        test("defersSendingWhileTransactionActive") {
            TransactionSynchronizationManager.initSynchronization()
            try {
                DummyEmailSender().send(Email("kim@splearn.app"), "subject", "body")

                TransactionSynchronizationManager.getSynchronizations() shouldHaveSize 1
            } finally {
                TransactionSynchronizationManager.clearSynchronization()
            }
        }
    })
