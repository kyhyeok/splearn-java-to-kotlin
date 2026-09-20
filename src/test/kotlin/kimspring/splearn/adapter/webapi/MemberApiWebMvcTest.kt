package kimspring.splearn.adapter.webapi

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kimspring.splearn.application.member.command.RegisterMemberCommand
import kimspring.splearn.application.member.command.UpdateMemberInfoCommand
import kimspring.splearn.application.member.usecase.MemberLifecycle
import kimspring.splearn.application.member.usecase.MemberModifier
import kimspring.splearn.application.member.usecase.MemberRegister
import kimspring.splearn.domain.member.InvalidActivationTokenException
import kimspring.splearn.domain.member.MemberFixture
import kimspring.splearn.domain.member.MemberNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.web.servlet.assertj.MockMvcTester
import tools.jackson.databind.ObjectMapper

@WebMvcTest(MemberApi::class)
class MemberApiWebMvcTest : FunSpec() {
    @TestConfiguration
    class Config {
        @Bean
        fun memberRegister(): MemberRegister = mockk()

        @Bean
        fun memberLifecycle(): MemberLifecycle = mockk()

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
    private lateinit var memberLifecycle: MemberLifecycle

    @Autowired
    private lateinit var memberModifier: MemberModifier

    private fun postRegister(request: RegisterMemberCommand) =
        mvcTester
            .post()
            .uri("/api/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))

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

        // 에러 응답 계약(code·fields)과 상태 코드 매핑을 고정한다. 이 계약은 OpenApiConfig 가 공개 문서로 선언한다
        test("registerFailResponseBody") {
            val request = RegisterMemberCommand("invalid email", "KimHyeok", "verysecret")

            assertThat(postRegister(request))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.code") { assertThat(it).isEqualTo("C002") }
                .hasPathSatisfying("$.fields[0].field") { assertThat(it).isEqualTo("email") }
        }

        test("registerFailPasswordOver72Bytes") {
            // 한글 25자 = 75바이트. 문자 수 검증은 통과하지만 BCrypt 한계(72바이트)를 넘는다
            val request = RegisterMemberCommand("kim@splearn.app", "KimHyeok", "가".repeat(25))

            assertThat(postRegister(request))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.fields[0].field") { assertThat(it).isEqualTo("password") }
        }

        test("registerConflictOnDuplicateKey") {
            val request = MemberFixture.createRegisterMemberCommand()
            every { memberRegister.register(request) } throws DuplicateKeyException("duplicate")

            assertThat(postRegister(request))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("C007")
        }

        test("registerBadRequestOnIllegalArgument") {
            val request = MemberFixture.createRegisterMemberCommand()
            every { memberRegister.register(request) } throws IllegalArgumentException("invalid")

            assertThat(postRegister(request))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("C002")
        }

        test("deactivateNotFound") {
            val memberId = 999L
            every { memberLifecycle.deactivate(memberId) } throws MemberNotFoundException(memberId)

            assertThat(mvcTester.patch().uri("/api/members/$memberId/deactivate"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("M001")
        }

        test("deactivateForbidden") {
            val memberId = 1L
            every { memberLifecycle.deactivate(memberId) } throws AccessDeniedException("denied")

            assertThat(mvcTester.patch().uri("/api/members/$memberId/deactivate"))
                .hasStatus(HttpStatus.FORBIDDEN)
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("C003")
        }

        test("deactivateInternalError") {
            val memberId = 1L
            every { memberLifecycle.deactivate(memberId) } throws IllegalStateException("boom")

            assertThat(mvcTester.patch().uri("/api/members/$memberId/deactivate"))
                .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("C001")
        }

        test("methodNotAllowed") {
            assertThat(mvcTester.get().uri("/api/members"))
                .hasStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("C004")
        }

        test("activate") {
            val memberId = 1L
            val token = "activation-token"
            val member = MemberFixture.createMember(memberId)
            every { memberLifecycle.activate(token) } returns member

            assertThat(
                mvcTester
                    .post()
                    .uri("/api/members/activate")
                    .param("token", token),
            ).hasStatusOk()
                .bodyJson()
                .extractingPath("$.memberId")
                .asNumber()
                .isEqualTo(memberId.toInt())

            verify { memberLifecycle.activate(token) }
        }

        test("activateFailInvalidToken") {
            val token = "no-such-token"
            every { memberLifecycle.activate(token) } throws InvalidActivationTokenException("유효하지 않은 활성화 토큰입니다.")

            assertThat(
                mvcTester
                    .post()
                    .uri("/api/members/activate")
                    .param("token", token),
            ).hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("M006")
        }

        test("deactivateConflictOnConcurrentModification") {
            val memberId = 1L
            every { memberLifecycle.deactivate(memberId) } throws OptimisticLockingFailureException("stale")

            assertThat(
                mvcTester
                    .patch()
                    .uri("/api/members/$memberId/deactivate"),
            ).hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("C006")
        }

        test("deactivate") {
            val memberId = 1L
            val member = MemberFixture.createMember(memberId)
            every { memberLifecycle.deactivate(memberId) } returns member

            assertThat(
                mvcTester
                    .patch()
                    .uri("/api/members/$memberId/deactivate"),
            ).hasStatusOk()
                .bodyJson()
                .extractingPath("$.memberId")
                .asNumber()
                .isEqualTo(memberId.toInt())

            verify { memberLifecycle.deactivate(memberId) }
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
