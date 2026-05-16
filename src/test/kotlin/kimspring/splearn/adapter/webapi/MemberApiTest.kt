package kimspring.splearn.adapter.webapi

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import kimspring.splearn.SplearnTestConfiguration
import kimspring.splearn.adapter.webapi.dto.MemberRegisterResponse
import kimspring.splearn.application.member.provided.MemberRegister
import kimspring.splearn.application.member.required.MemberRepository
import kimspring.splearn.domain.member.MemberFixture
import kimspring.splearn.domain.member.MemberRegisterRequest
import kimspring.splearn.domain.member.MemberStatus
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(SplearnTestConfiguration::class)
class MemberApiTest : FunSpec() {
    @Autowired
    private lateinit var mvcTester: MockMvcTester

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var memberRegister: MemberRegister

    init {
        extension(SpringExtension())

        test("register") {
            val request: MemberRegisterRequest = MemberFixture.createMemberRegisterRequest()
            val requestJson = objectMapper.writeValueAsString(request)

            val result =
                mvcTester
                    .post()
                    .uri("/api/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .exchange()

            assertThat(result)
                .hasStatusOk()
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

        test("duplicateEmail") {
            memberRegister.register(MemberFixture.createMemberRegisterRequest())

            val request: MemberRegisterRequest = MemberFixture.createMemberRegisterRequest()
            val requestJson = objectMapper.writeValueAsString(request)

            val result =
                mvcTester
                    .post()
                    .uri("/api/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .exchange()

            assertThat(result)
                .apply(print())
                .hasStatus(HttpStatus.CONFLICT)
        }
    }
}
