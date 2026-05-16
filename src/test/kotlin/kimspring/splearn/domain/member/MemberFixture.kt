package kimspring.splearn.domain.member

object MemberFixture {
    fun createMemberRegisterRequest(email: String): MemberRegisterRequest =
        MemberRegisterRequest(email, "KimHyeok", "verysecret")

    fun createMemberRegisterRequest(): MemberRegisterRequest = createMemberRegisterRequest("kim@gmail.com")

    fun createPasswordEncoder(): PasswordEncoder =
        object : PasswordEncoder {
            override fun encode(password: String): String = password.uppercase()

            override fun matches(
                password: String,
                passwordHash: String,
            ): Boolean = encode(password) == passwordHash
        }

    fun createMember(): Member = Member.register(createMemberRegisterRequest(), createPasswordEncoder())

    fun createMember(id: Long): Member =
        Member.register(createMemberRegisterRequest(), createPasswordEncoder()).copy(id = id)

    fun createMember(email: String): Member =
        Member.register(createMemberRegisterRequest(email), createPasswordEncoder())
}
