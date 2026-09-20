package kimspring.splearn.application.member.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.validation.ConstraintViolationException
import kimspring.splearn.application.member.command.RegisterMemberCommand
import kimspring.splearn.domain.member.DuplicateEmailException
import kimspring.splearn.domain.member.MemberFixture
import kimspring.splearn.domain.member.MemberStatus
import kimspring.splearn.support.stereotype.ApplicationServiceTest
import org.springframework.beans.factory.annotation.Autowired

@ApplicationServiceTest
class MemberRegisterTest : FunSpec() {
    @Autowired
    private lateinit var memberRegister: MemberRegister

    init {
        extension(SpringExtension())

        test("register") {
            val member = memberRegister.register(MemberFixture.createRegisterMemberCommand())

            member.shouldNotBeNull()
            member.status shouldBe MemberStatus.PENDING
        }

        test("duplicateEmailFail") {
            val command = MemberFixture.createRegisterMemberCommand()
            memberRegister.register(command)

            shouldThrow<DuplicateEmailException> {
                memberRegister.register(command)
            }
        }

        // 이메일 대소문자는 구분하지 않는다. DB 콜레이션(H2 구분·MySQL 무시)과 무관하게 도메인이 보장한다
        test("duplicateEmailFailIgnoringCase") {
            val registered = memberRegister.register(MemberFixture.createRegisterMemberCommand("Kim.Lee@Splearn.App"))
            registered.email.address shouldBe "kim.lee@splearn.app"

            shouldThrow<DuplicateEmailException> {
                memberRegister.register(MemberFixture.createRegisterMemberCommand("kim.lee@splearn.app"))
            }
        }

        test("memberRegisterCommandFail") {
            checkValidation(RegisterMemberCommand("kim@splearn.app", "Kim", "secret1234"))
            checkValidation(RegisterMemberCommand("kim@splearn.app", "KimLongNameSplearnTestCode", "secret1234"))
            checkValidation(RegisterMemberCommand("kimsplearn.app", "KimLongName", "secret1234"))
            checkValidation(RegisterMemberCommand("kim#splearn.app", "KimLongName", "secret"))
        }
    }

    private fun checkValidation(invalid: RegisterMemberCommand) {
        shouldThrow<ConstraintViolationException> { memberRegister.register(invalid) }
    }
}
