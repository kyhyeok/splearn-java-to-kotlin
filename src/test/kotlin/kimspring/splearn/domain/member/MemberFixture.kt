package kimspring.splearn.domain.member

import kimspring.splearn.application.member.command.RegisterMemberCommand
import kimspring.splearn.domain.shared.Email
import java.time.LocalDateTime

object MemberFixture {
    fun createRegisterMemberCommand(email: String): RegisterMemberCommand =
        RegisterMemberCommand(email, "KimHyeok", "verysecret")

    fun createRegisterMemberCommand(): RegisterMemberCommand = createRegisterMemberCommand("kim@gmail.com")

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
        return Member.register(
            email = Email(command.email),
            nickname = command.nickname,
            password = command.password,
            encoder = createPasswordEncoder(),
            now = FIXED_NOW,
        )
    }

    fun createMember(id: Long): Member = createMember().copy(id = id)

    fun createMember(email: String): Member {
        val command = createRegisterMemberCommand(email)
        return Member.register(
            email = Email(command.email),
            nickname = command.nickname,
            password = command.password,
            encoder = createPasswordEncoder(),
            now = FIXED_NOW,
        )
    }
}
