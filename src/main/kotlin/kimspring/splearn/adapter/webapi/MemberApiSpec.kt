package kimspring.splearn.adapter.webapi

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kimspring.splearn.adapter.webapi.dto.MemberRegisterResponse
import kimspring.splearn.adapter.webapi.dto.MemberResponse
import kimspring.splearn.application.member.command.RegisterMemberCommand
import kimspring.splearn.application.member.command.UpdateMemberInfoCommand
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "회원", description = "회원 관리 API")
@RequestMapping("/api/members")
interface MemberApiSpec {
    @Operation(
        summary = "회원 가입",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "회원 가입 성공",
                headers = [Header(name = "Location", description = "생성된 회원 URI", schema = Schema(type = "string"))],
            ),
            ApiResponse(responseCode = "409", description = "이메일 중복"),
        ],
    )
    @PostMapping
    fun register(
        @RequestBody @Valid request: RegisterMemberCommand,
    ): ResponseEntity<MemberRegisterResponse>

    @Operation(summary = "회원 활성화")
    @PatchMapping("/{memberId}/activate")
    fun activate(
        @PathVariable memberId: Long,
    ): MemberResponse

    @Operation(summary = "회원 비활성화")
    @PatchMapping("/{memberId}/deactivate")
    fun deactivate(
        @PathVariable memberId: Long,
    ): MemberResponse

    @Operation(
        summary = "회원 정보 수정",
        responses = [ApiResponse(responseCode = "409", description = "닉네임 또는 프로필 주소 중복")],
    )
    @PatchMapping("/{memberId}")
    fun updateInfo(
        @PathVariable memberId: Long,
        @RequestBody @Valid request: UpdateMemberInfoCommand,
    ): MemberResponse
}
