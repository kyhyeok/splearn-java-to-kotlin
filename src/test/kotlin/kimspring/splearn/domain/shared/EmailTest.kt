package kimspring.splearn.domain.shared

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class EmailTest :
    FunSpec({
        test("equality") {
            val email1 = Email("kim@splearn.app")
            val email2 = Email("kim@splearn.app")

            email1 shouldBe email2
        }

        test("normalizesCase") {
            val email = Email("Kim.Lee@Splearn.App")

            email.address shouldBe "kim.lee@splearn.app"
            email shouldBe Email("kim.lee@splearn.app")
        }

        test("invalidFormatFail") {
            shouldThrow<IllegalArgumentException> { Email("kimsplearn.app") }
            shouldThrow<IllegalArgumentException> { Email("kim@splearn") }
        }
    })
