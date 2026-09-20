package kimspring.splearn.application.member.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.application.member.command.UpdateMemberInfoCommand
import kimspring.splearn.domain.member.DuplicateProfileException
import kimspring.splearn.support.test.BaseApplicationServiceTest
import org.springframework.beans.factory.annotation.Autowired

class MemberModifierTest : BaseApplicationServiceTest() {
    @Autowired
    private lateinit var memberModifier: MemberModifier

    init {
        test("updateInfo") {
            val memberId = requireNotNull(prepareActiveMember().id)

            val command = UpdateMemberInfoCommand("Hyeok", "kim001", "자기소개")
            val updated = memberModifier.updateInfo(memberId, command)

            val profile = updated.detail.profile.shouldNotBeNull()
            profile.address shouldBe command.profileAddress
        }

        test("updateInfoFail") {
            val memberId = requireNotNull(prepareActiveMember().id)
            memberModifier.updateInfo(memberId, UpdateMemberInfoCommand("Hyeok", "kim001", "자기소개"))

            val member2Id = requireNotNull(prepareActiveMember().id)

            // member2는 기존의 member와 같은 프로필 주소를 사용할 수 없다
            shouldThrow<DuplicateProfileException> {
                memberModifier.updateInfo(member2Id, UpdateMemberInfoCommand("Kimmy", "kim001", "자기소개임"))
            }

            // 다른 프로필 주소로는 변경 가능
            memberModifier.updateInfo(member2Id, UpdateMemberInfoCommand("Kimmy", "kim002", "자기소개임"))

            // 기존 프로필 주소를 바꾸는 것도 가능
            memberModifier.updateInfo(memberId, UpdateMemberInfoCommand("Kimmy", "kim001", "자기소개임"))

            // 프로필 주소 중복은 허용하지 않음
            shouldThrow<DuplicateProfileException> {
                memberModifier.updateInfo(memberId, UpdateMemberInfoCommand("Kimmy", "kim002", "자기소개임"))
            }

            // 프로필 주소를 제거하는 것도 가능
            memberModifier.updateInfo(memberId, UpdateMemberInfoCommand("Kimmy", "", "자기소개임"))
            memberModifier.updateInfo(member2Id, UpdateMemberInfoCommand("Kimmy", "", "자기소개임"))
        }
    }
}
