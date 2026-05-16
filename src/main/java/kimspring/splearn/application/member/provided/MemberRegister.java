package kimspring.splearn.application.member.provided;

import jakarta.validation.Valid;
import kimspring.splearn.domain.member.Member;
import kimspring.splearn.domain.member.MemberInfoUpdateRequest;
import kimspring.splearn.domain.member.MemberRegisterRequest;

/**
 * 회원의 등록과 돤련된 기능을 제공한다
 */
public interface MemberRegister {
    Member register(@Valid MemberRegisterRequest registerRequest);

    Member activate(Long memberId);

    Member deactivate(Long memberId);

    Member updateInfo(Long memberId, @Valid MemberInfoUpdateRequest updateRequest);
}
