package kimspring.splearn.domain.member

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ProfileTest :
    FunSpec({
        test("profile") {
            Profile("kim")
            Profile("kim2")
            Profile("0044")
            Profile("")
        }

        test("profileFail") {
            shouldThrow<IllegalArgumentException> { Profile("toolongtoolongtoolong") }
            shouldThrow<IllegalArgumentException> { Profile("KIM") }
            shouldThrow<IllegalArgumentException> { Profile("킴") }
        }

        test("url") {
            val profile = Profile("kim")
            profile.url() shouldBe "@kim"
        }
    })
