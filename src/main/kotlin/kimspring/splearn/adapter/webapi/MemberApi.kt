package kimspring.splearn.adapter.webapi

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kimspring.splearn.adapter.webapi.dto.MemberRegisterResponse
import kimspring.splearn.adapter.webapi.dto.MemberResponse
import kimspring.splearn.application.member.command.RegisterMemberCommand
import kimspring.splearn.application.member.command.UpdateMemberInfoCommand
import kimspring.splearn.application.member.usecase.MemberRegister
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "회원", description = "회원 관리 API")
@RestController
class MemberApi(
    private val memberRegister: MemberRegister,
) {
    @Operation(summary = "회원 가입")
    @ApiResponse(responseCode = "409", description = "이메일 중복")
    @PostMapping("/api/members")
    fun register(
        @RequestBody @Valid request: RegisterMemberCommand,
    ): MemberRegisterResponse {
        val member = memberRegister.register(request)
        return MemberRegisterResponse.of(member)
    }

    @Operation(summary = "회원 활성화")
    @PatchMapping("/api/members/{memberId}/activate")
    fun activate(
        @PathVariable memberId: Long,
    ): MemberResponse = MemberResponse.of(memberRegister.activate(memberId))

    @Operation(summary = "회원 비활성화")
    @PatchMapping("/api/members/{memberId}/deactivate")
    fun deactivate(
        @PathVariable memberId: Long,
    ): MemberResponse = MemberResponse.of(memberRegister.deactivate(memberId))

    @Operation(summary = "회원 정보 수정")
    @ApiResponse(responseCode = "409", description = "닉네임 또는 프로필 주소 중복")
    @PatchMapping("/api/members/{memberId}")
    fun updateInfo(
        @PathVariable memberId: Long,
        @RequestBody @Valid request: UpdateMemberInfoCommand,
    ): MemberResponse = MemberResponse.of(memberRegister.updateInfo(memberId, request))
}
