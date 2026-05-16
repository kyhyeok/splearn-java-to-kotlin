package kimspring.splearn.domain.shared

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class EmailTest :
    FunSpec({
        test("equality") {
            val email1 = Email("kim@splearn.app")
            val email2 = Email("kim@splearn.app")

            email1 shouldBe email2
        }
    })
