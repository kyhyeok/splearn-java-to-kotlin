package kimspring.splearn.adapter.integration

import io.kotest.core.spec.style.FunSpec
import kimspring.splearn.domain.shared.Email

class DummyEmailSenderTest :
    FunSpec({
        test("dummyEmailSender") {
            DummyEmailSender().send(Email("kim@splearn.app"), "subject", "body")
        }
    })
