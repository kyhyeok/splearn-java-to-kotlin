package kimspring.splearn.adapter.webapi

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kimspring.splearn.application.member.command.RegisterMemberCommand
import kimspring.splearn.application.member.command.UpdateMemberInfoCommand
import kimspring.splearn.application.member.usecase.MemberModifier
import kimspring.splearn.application.member.usecase.MemberRegister
import kimspring.splearn.domain.member.MemberFixture
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester
import tools.jackson.databind.ObjectMapper

@WebMvcTest(MemberApi::class)
class MemberApiWebMvcTest : FunSpec() {
    @TestConfiguration
    class Config {
        @Bean
        fun memberRegister(): MemberRegister = mockk()

        @Bean
        fun memberModifier(): MemberModifier = mockk()
    }

    @Autowired
    private lateinit var mvcTester: MockMvcTester

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var memberRegister: MemberRegister

    @Autowired
    private lateinit var memberModifier: MemberModifier

    init {
        extension(SpringExtension())
        afterEach { clearAllMocks() }

        test("register") {
            val member = MemberFixture.createMember(1L)
            val request = MemberFixture.createRegisterMemberCommand()
            every { memberRegister.register(request) } returns member

            val requestJson = objectMapper.writeValueAsString(request)
            val result =
                mvcTester
                    .post()
                    .uri("/api/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .exchange()

            assertThat(result)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.memberId")
                .asNumber()
                .isEqualTo(1)

            verify { memberRegister.register(request) }
        }

        test("registerFail") {
            val request = RegisterMemberCommand("invalid email", "KimHyeok", "verysecret")
            val requestJson = objectMapper.writeValueAsString(request)

            assertThat(
                mvcTester
                    .post()
                    .uri("/api/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson),
            ).hasStatus(HttpStatus.BAD_REQUEST)
        }

        test("activate") {
            val memberId = 1L
            val member = MemberFixture.createMember(memberId)
            every { memberRegister.activate(memberId) } returns member

            assertThat(
                mvcTester
                    .patch()
                    .uri("/api/members/$memberId/activate"),
            ).hasStatusOk()
                .bodyJson()
                .extractingPath("$.memberId")
                .asNumber()
                .isEqualTo(memberId.toInt())

            verify { memberRegister.activate(memberId) }
        }

        test("deactivate") {
            val memberId = 1L
            val member = MemberFixture.createMember(memberId)
            every { memberRegister.deactivate(memberId) } returns member

            assertThat(
                mvcTester
                    .patch()
                    .uri("/api/members/$memberId/deactivate"),
            ).hasStatusOk()
                .bodyJson()
                .extractingPath("$.memberId")
                .asNumber()
                .isEqualTo(memberId.toInt())

            verify { memberRegister.deactivate(memberId) }
        }

        test("updateInfo") {
            val memberId = 1L
            val member = MemberFixture.createMember(memberId)
            val request = UpdateMemberInfoCommand("validNick", "profile", "introduction")
            every { memberModifier.updateInfo(memberId, request) } returns member

            val requestJson = objectMapper.writeValueAsString(request)

            assertThat(
                mvcTester
                    .patch()
                    .uri("/api/members/$memberId")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson),
            ).hasStatusOk()
                .bodyJson()
                .extractingPath("$.memberId")
                .asNumber()
                .isEqualTo(memberId.toInt())

            verify { memberModifier.updateInfo(memberId, request) }
        }

        test("updateInfoFail") {
            val memberId = 1L
            val request = UpdateMemberInfoCommand("ab", "profile", "introduction")
            val requestJson = objectMapper.writeValueAsString(request)

            assertThat(
                mvcTester
                    .patch()
                    .uri("/api/members/$memberId")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson),
            ).hasStatus(HttpStatus.BAD_REQUEST)
        }
    }
}
