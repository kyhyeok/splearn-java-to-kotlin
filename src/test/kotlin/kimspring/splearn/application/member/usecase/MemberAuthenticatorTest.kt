package kimspring.splearn.application.member.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import jakarta.validation.ConstraintViolationException
import kimspring.splearn.SplearnTestConfiguration
import kimspring.splearn.application.member.command.LoginMemberCommand
import kimspring.splearn.application.member.command.RegisterMemberCommand
import kimspring.splearn.domain.member.LoginFailedException
import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberFixture
import kimspring.splearn.domain.member.MemberStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Import(SplearnTestConfiguration::class)
class MemberAuthenticatorTest : FunSpec() {
    @Autowired
    private lateinit var memberAuthenticator: MemberAuthenticator

    @Autowired
    private lateinit var memberRegister: MemberRegister

    @Autowired
    private lateinit var memberLifecycle: MemberLifecycle

    init {
        extension(SpringExtension())

        test("login") {
            val command = MemberFixture.createRegisterMemberCommand()
            registerActivatedMember(command)

            val member = memberAuthenticator.login(LoginMemberCommand(command.email, command.password))

            member.email.address shouldBe command.email
            member.status shouldBe MemberStatus.ACTIVE
        }

        test("loginFailNotActive") {
            val command = MemberFixture.createRegisterMemberCommand()
            memberRegister.register(command)

            shouldThrow<LoginFailedException> {
                memberAuthenticator.login(LoginMemberCommand(command.email, command.password))
            }
        }

        test("loginFailEmailNotExist") {
            val command = MemberFixture.createRegisterMemberCommand()
            registerActivatedMember(command)

            shouldThrow<LoginFailedException> {
                memberAuthenticator.login(LoginMemberCommand("notexist@splearn.app", command.password))
            }
        }

        test("loginFailWrongPassword") {
            val command = MemberFixture.createRegisterMemberCommand()
            registerActivatedMember(command)

            shouldThrow<LoginFailedException> {
                memberAuthenticator.login(LoginMemberCommand(command.email, "wrongpassword"))
            }
        }

        test("loginCommandFail") {
            checkValidation(LoginMemberCommand("kimsplearn.app", "verysecret"))
            checkValidation(LoginMemberCommand("kim@splearn.app", "secret"))
        }
    }

    private fun checkValidation(invalid: LoginMemberCommand) {
        shouldThrow<ConstraintViolationException> { memberAuthenticator.login(invalid) }
    }

    private fun registerActivatedMember(command: RegisterMemberCommand): Member {
        val member = memberRegister.register(command)
        return memberLifecycle.activate(requireNotNull(member.id))
    }
}
