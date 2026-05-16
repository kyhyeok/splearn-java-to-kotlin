package kimspring.splearn.application.member.provided;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import kimspring.splearn.SplearnTestConfiguration;
import kimspring.splearn.domain.member.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
@Import(SplearnTestConfiguration.class)
record MemberRegisterTest(MemberRegister memberRegister, EntityManager entityManager) {
    @Test
    void register() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThat(member).isNotNull();
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @Test
    void duplicateEmailFail() {
        memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThatThrownBy(() -> memberRegister.register(MemberFixture.createMemberRegisterRequest()))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void activate() {
        Member member = registerMember();

        member = memberRegister.activate(member.getId());
        entityManager.flush();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(member.getDetail().getActivatedAt()).isNotNull();
    }


    @Test
    void deactivate() {
        Member member = registerMember();

        member = memberRegister.activate(member.getId());
        entityManager.flush();
        entityManager.clear();

        member = memberRegister.deactivate(member.getId());
        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
        assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
    }

    @Test
    void updateInfo() {
        Member member = registerMember();

        member = memberRegister.activate(member.getId());
        entityManager.flush();
        entityManager.clear();

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest("Hyeok", "kim001", "자기소개");
        member = memberRegister.updateInfo(member.getId(), request);

        assertThat(member.getDetail().getProfile().address()).isEqualTo(request.profileAddress());
    }

    @Test
    void updateInfoFail() {
        Member member = registerMember();
        memberRegister.activate(member.getId());
        memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("Hyeok", "kim001", "자기소개"));

        Member member2 = registerMember("kiim@splearn.app");
        memberRegister.activate(member2.getId());
        entityManager.flush();
        entityManager.clear();

        // member2는 기존의 member와 같은 프로필 주소를 사용할 수 없다
        assertThatThrownBy(() -> memberRegister.updateInfo(member2.getId(), new MemberInfoUpdateRequest("Kimmy", "kim001", "자기소개임")))
                .isInstanceOf(DuplicateProfileException.class);

        // 다른 프로필 주소로는 변경 가능
        memberRegister.updateInfo(member2.getId(), new MemberInfoUpdateRequest("Kimmy", "kim002", "자기소개임"));

        // 기존 프로필 주소를 바꾸는 것도 가능
        memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("Kimmy", "kim001", "자기소개임"));

        // 프로필 주소를 제거하는 것도 가능
        memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("Kimmy", "", "자기소개임"));

        // 프로필 주소 중복은 허용하지 않음
        assertThatThrownBy(() ->
                memberRegister.updateInfo(member.getId(),
                        new MemberInfoUpdateRequest("Kimmy", "kim002", "자기소개임")))
                .isInstanceOf(DuplicateProfileException.class);
    }

    @Test
    void memberRegisterRequestFail() {
        checkValidation(new MemberRegisterRequest("kim@splearn.app", "Kim", "secret1234"));
        checkValidation(new MemberRegisterRequest("kim@splearn.app", "KimLongNameSplearnTestCode", "secret1234"));
        checkValidation(new MemberRegisterRequest("kimsplearn.app", "KimLongName", "secret1234"));
        checkValidation(new MemberRegisterRequest("kim#splearn.app", "KimLongName", "secret"));
    }

    private void checkValidation(MemberRegisterRequest invalid) {
        assertThatThrownBy(() -> memberRegister.register(invalid))
                .isInstanceOf(ConstraintViolationException.class);
    }

    private Member registerMember() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
        entityManager.flush();
        entityManager.clear();
        return member;
    }

    private Member registerMember(String email) {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest(email));
        entityManager.flush();
        entityManager.clear();
        return member;
    }
}
