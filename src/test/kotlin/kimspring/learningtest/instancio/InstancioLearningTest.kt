package kimspring.learningtest.instancio

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import org.instancio.Instancio
import org.instancio.Model
import org.instancio.Select.field

class InstancioLearningTest :
    FunSpec({
        test("user") {
            val user =
                Instancio
                    .of(User::class.java)
                    .ignore(field(User::class.java, User::id.name))
                    .generate(field(User::class.java, User::email.name)) { gen -> gen.net().email() }
                    .set(field(User::class.java, User::status.name), UserStatus.PENDING)
                    .create()

            user.id.shouldBeNull()
            user.email.shouldNotBeEmpty()
            user.name.shouldNotBeEmpty()
            user.status shouldBe UserStatus.PENDING
        }

        test("userModel") {
            val model: Model<User> =
                Instancio
                    .of(User::class.java)
                    .ignore(field(User::class.java, User::id.name))
                    .generate(field(User::class.java, User::email.name)) { gen -> gen.net().email() }
                    .set(field(User::class.java, User::status.name), UserStatus.PENDING)
                    .toModel()

            repeat(100) {
                val user = Instancio.of(model).create()

                user.id.shouldBeNull()
                user.email.shouldNotBeEmpty()
                user.name.shouldNotBeEmpty()
                user.status shouldBe UserStatus.PENDING
            }
        }

        test("annotation") {
            val request = Instancio.of(UserRegisterRequest::class.java).create()

            request.email.shouldNotBeEmpty()
            request.nickname.shouldNotBeEmpty()
            request.password.length shouldBeInRange 8..100
        }
    })
