package kimspring.splearn.application.member.provided;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import kimspring.splearn.SplearnTestConfiguration;
import kimspring.splearn.domain.member.Member;
import kimspring.splearn.domain.member.MemberFixture;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@Import(SplearnTestConfiguration.class)
record MemberFinderTest(
        MemberFinder memberFinder,
        MemberRegister memberRegister,
        EntityManager entityManager
) {

    @Test
    void find() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
        entityManager.flush();
        entityManager.clear();

        Member found = memberFinder.find(member.getId());

        assertThat(member.getId()).isEqualTo(found.getId());
    }

    @Test
    void findFail() {
        assertThatThrownBy(() -> memberFinder.find(9999L))
            .isInstanceOf(IllegalArgumentException.class);
    }
}