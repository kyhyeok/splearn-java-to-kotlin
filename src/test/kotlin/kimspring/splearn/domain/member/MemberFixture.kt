package kimspring.splearn.domain.member

import kimspring.splearn.application.member.command.RegisterMemberCommand
import org.instancio.Instancio
import org.instancio.Select.field
import java.time.LocalDateTime

object MemberFixture {
    fun createRegisterMemberCommand(email: String): RegisterMemberCommand =
        Instancio
            .of(RegisterMemberCommand::class.java)
            .set(field(RegisterMemberCommand::class.java, RegisterMemberCommand::email.name), email)
            .create()

    fun createRegisterMemberCommand(): RegisterMemberCommand = createRegisterMemberCommand(randomEmail())

    private fun randomEmail(): String =
        Instancio
            .gen()
            .net()
            .email()
            .get()

    fun createPasswordEncoder(): PasswordEncoder =
        object : PasswordEncoder {
            override fun encode(password: String): String = password.uppercase()

            override fun matches(
                password: String,
                passwordHash: String,
            ): Boolean = encode(password) == passwordHash
        }

    private val FIXED_NOW = LocalDateTime.of(2024, 1, 1, 0, 0)

    fun createMember(): Member {
        val command = createRegisterMemberCommand()
        return Member.register(command.toInfo(), createPasswordEncoder(), FIXED_NOW)
    }

    fun createMember(id: Long): Member = createMember().copy(id = id)

    fun createMember(email: String): Member {
        val command = createRegisterMemberCommand(email)
        return Member.register(command.toInfo(), createPasswordEncoder(), FIXED_NOW)
    }

    fun createActiveMember(id: Long = 1L): Member = createMember(id).activate(FIXED_NOW)
}
