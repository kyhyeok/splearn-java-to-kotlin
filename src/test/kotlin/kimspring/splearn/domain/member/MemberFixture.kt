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

    // Email 정규식(TLD 2~7자)을 항상 만족하는 형태로 직접 만든다. 라이브러리의 이메일 생성 규칙 변화에 흔들리지 않는다
    private fun randomEmail(): String = "${Instancio.gen().string().lowerCase().length(16).get()}@splearn.app"

    // 인메모리 도메인 테스트용 식별자. 하드코딩 PK 대신 랜덤 값을 쓴다 — DB 저장 테스트는 id 없는 픽스처로 실제 채번한다
    fun randomId(): Long =
        Instancio
            .gen()
            .longs()
            .range(1L, Long.MAX_VALUE)
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

    fun createActiveMember(id: Long = randomId()): Member = createMember(id).activate(FIXED_NOW)
}
