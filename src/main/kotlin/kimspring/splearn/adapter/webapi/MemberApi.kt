package kimspring.splearn.adapter.webapi

import jakarta.validation.Valid
import kimspring.splearn.adapter.webapi.dto.MemberRegisterResponse
import kimspring.splearn.adapter.webapi.dto.MemberResponse
import kimspring.splearn.application.member.provided.MemberRegister
import kimspring.splearn.domain.member.MemberInfoUpdateRequest
import kimspring.splearn.domain.member.MemberRegisterRequest
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberApi(
    private val memberRegister: MemberRegister,
) {
    @PostMapping("/api/members")
    fun register(
        @RequestBody @Valid request: MemberRegisterRequest,
    ): MemberRegisterResponse {
        val member = memberRegister.register(request)
        return MemberRegisterResponse.of(member)
    }

    @PatchMapping("/api/members/{memberId}/activate")
    fun activate(
        @PathVariable memberId: Long,
    ): MemberResponse = MemberResponse.of(memberRegister.activate(memberId))

    @PatchMapping("/api/members/{memberId}/deactivate")
    fun deactivate(
        @PathVariable memberId: Long,
    ): MemberResponse = MemberResponse.of(memberRegister.deactivate(memberId))

    @PatchMapping("/api/members/{memberId}")
    fun updateInfo(
        @PathVariable memberId: Long,
        @RequestBody @Valid request: MemberInfoUpdateRequest,
    ): MemberResponse = MemberResponse.of(memberRegister.updateInfo(memberId, request))
}
