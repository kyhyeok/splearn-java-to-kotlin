package kimspring.splearn.adapter.webapi

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import kimspring.splearn.adapter.webapi.dto.MemberRegisterResponse
import kimspring.splearn.application.member.command.RegisterMemberCommand
import kimspring.splearn.application.member.port.MemberRepository
import kimspring.splearn.application.member.usecase.MemberRegister
import kimspring.splearn.domain.member.MemberFixture
import kimspring.splearn.domain.member.MemberStatus
import kimspring.splearn.support.stereotype.WebApiAdapterTest
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester
import tools.jackson.databind.ObjectMapper
import java.time.Instant
import java.util.Base64
import java.util.Date

@WebApiAdapterTest
class MemberApiTest : FunSpec() {
    @Autowired
    private lateinit var mvcTester: MockMvcTester

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var memberRegister: MemberRegister

    @Value("\${jwt.secret}")
    private lateinit var jwtSecret: String

    @Value("\${jwt.issuer}")
    private lateinit var jwtIssuer: String

    @Value("\${jwt.audience}")
    private lateinit var jwtAudience: String

    private fun postRegister(bearerToken: String) =
        mvcTester
            .post()
            .uri("/api/members")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $bearerToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(MemberFixture.createRegisterMemberCommand()))
            .exchange()

    private fun signedToken(
        issuer: String,
        audience: String,
    ): String {
        val claims =
            JWTClaimsSet
                .Builder()
                .issuer(issuer)
                .audience(audience)
                .subject("1")
                .expirationTime(Date.from(Instant.now().plusSeconds(60)))
                .build()
        return SignedJWT(JWSHeader(JWSAlgorithm.HS256), claims)
            .apply { sign(MACSigner(Base64.getDecoder().decode(jwtSecret))) }
            .serialize()
    }

    init {
        extension(SpringExtension())

        // permitAll 경로라도 Bearer 토큰이 붙어 있으면 검증한다. 필터 체인의 401 은 ControllerAdvice 를 거치지 않는다
        test("rejectsMalformedBearerToken") {
            assertThat(postRegister("not-a-jwt")).hasStatus(HttpStatus.UNAUTHORIZED)
        }

        // 같은 키로 서명됐어도 이 서비스가 발급한 토큰(iss·aud)이 아니면 거절한다
        test("rejectsTokenOfAnotherIssuerOrAudience") {
            assertThat(postRegister(signedToken("other-issuer", jwtAudience))).hasStatus(HttpStatus.UNAUTHORIZED)
            assertThat(postRegister(signedToken(jwtIssuer, "other-audience"))).hasStatus(HttpStatus.UNAUTHORIZED)
        }

        test("acceptsTokenOfThisService") {
            assertThat(postRegister(signedToken(jwtIssuer, jwtAudience))).hasStatus(HttpStatus.CREATED)
        }

        test("register") {
            val request: RegisterMemberCommand = MemberFixture.createRegisterMemberCommand()
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
                .hasPathSatisfying("$.memberId") { assertThat(it).isNotNull() }
                .hasPathSatisfying("$.email") { assertThat(it).isEqualTo(request.email) }

            val response = objectMapper.readValue(result.response.contentAsString, MemberRegisterResponse::class.java)

            val foundMember =
                memberRepository.findById(response.memberId!!)
                    ?: throw NoSuchElementException()

            foundMember.email.address shouldBe request.email
            foundMember.nickname shouldBe request.nickname
            foundMember.status shouldBe MemberStatus.PENDING
        }

        // 가입 → 메일 토큰 → 활성화 흐름. 순번 id 로는 활성화 경로가 없다
        test("activateWithToken") {
            val registered = memberRegister.register(MemberFixture.createRegisterMemberCommand())
            val token = requireNotNull(registered.activationToken)

            assertThat(
                mvcTester
                    .post()
                    .uri("/api/members/activate")
                    .param("token", token),
            ).hasStatusOk()
                .bodyJson()
                .extractingPath("$.memberId")
                .asNumber()
                .isEqualTo(registered.id!!.toInt())

            memberRepository.getById(registered.id!!).status shouldBe MemberStatus.ACTIVE
        }

        test("duplicateEmail") {
            val request: RegisterMemberCommand = MemberFixture.createRegisterMemberCommand()
            memberRegister.register(request)

            val requestJson = objectMapper.writeValueAsString(request)

            val result =
                mvcTester
                    .post()
                    .uri("/api/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .exchange()

            assertThat(result)
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("M002")
        }
    }
}
