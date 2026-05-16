package kimspring.splearn.adapter.webapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kimspring.splearn.adapter.webapi.dto.MemberRegisterResponse;
import kimspring.splearn.application.member.provided.MemberRegister;
import kimspring.splearn.application.member.required.MemberRepository;
import kimspring.splearn.domain.member.Member;
import kimspring.splearn.domain.member.MemberFixture;
import kimspring.splearn.domain.member.MemberRegisterRequest;
import kimspring.splearn.domain.member.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

import static kimspring.splearn.AssertThatUtils.equalsTo;
import static kimspring.splearn.AssertThatUtils.notNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
class MemberApiTest {
    final MockMvcTester mvcTester;

    final ObjectMapper objectMapper;

    final MemberRepository memberRepository;

    final MemberRegister memberRegister;

    @Test
    void register() throws JsonProcessingException, UnsupportedEncodingException {
        MemberRegisterRequest request = MemberFixture.createMemberRegisterRequest();
        String requestJson = objectMapper.writeValueAsString(request);


        MvcTestResult result = mvcTester.post()
                .uri("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson).exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.memberId", notNull())
                .hasPathSatisfying("$.email", equalsTo(request));

        MemberRegisterResponse response = 
                objectMapper.readValue(result.getResponse().getContentAsString(), MemberRegisterResponse.class);

        Member foundMember = memberRepository.findById(response.memberId()).orElseThrow();

        assertThat(foundMember.getEmail().address()).isEqualTo(request.email());
        assertThat(foundMember.getNickname()).isEqualTo(request.nickname());
        assertThat(foundMember.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @Test
    void duplicateEmail() throws JsonProcessingException {
        memberRegister.register(MemberFixture.createMemberRegisterRequest());

        MemberRegisterRequest request = MemberFixture.createMemberRegisterRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post()
                .uri("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson).exchange();

        assertThat(result)
                .apply(print())
                .hasStatus(HttpStatus.CONFLICT);
    }
}
