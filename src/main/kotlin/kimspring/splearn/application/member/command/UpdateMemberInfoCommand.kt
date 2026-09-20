package kimspring.splearn.application.member.command

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kimspring.splearn.domain.member.Profile

data class UpdateMemberInfoCommand(
    @field:Size(min = 5, max = 20) val nickname: String,
    // 빈 문자열은 "프로필 주소 없음"이라 허용한다
    @field:NotNull
    @field:Size(max = Profile.MAX_ADDRESS_LENGTH)
    @field:Pattern(regexp = "(" + Profile.ADDRESS_REGEX + ")?")
    val profileAddress: String,
    @field:NotNull val introduction: String,
)
