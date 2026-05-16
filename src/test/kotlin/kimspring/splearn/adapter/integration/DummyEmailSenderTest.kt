package kimspring.splearn.adapter.integration

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.shared.Email
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class DummyEmailSenderTest :
    FunSpec({
        test("dummyEmailSender") {
            val outputStream = ByteArrayOutputStream()
            val originalOut = System.out
            System.setOut(PrintStream(outputStream))

            try {
                DummyEmailSender().send(Email("kim@splearn.app"), "subject", "body")
            } finally {
                System.setOut(originalOut)
            }

            outputStream.toString().trim() shouldBe "DummyEmailSender.send email: Email(address=kim@splearn.app)"
        }
    })
