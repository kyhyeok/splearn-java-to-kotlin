package kimspring.splearn.adapter.security

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SecurePasswordEncoderTest :
    FunSpec({
        test("securePasswordEncoder") {
            val securePasswordEncoder = SecurePasswordEncoder()

            val passwordHash = securePasswordEncoder.encode("secret")

            securePasswordEncoder.matches("secret", passwordHash) shouldBe true
            securePasswordEncoder.matches("wrong", passwordHash) shouldBe false
        }
    })
