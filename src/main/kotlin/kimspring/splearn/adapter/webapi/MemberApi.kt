package kimspring.splearn.adapter.webapi

import kimspring.splearn.adapter.webapi.dto.MemberRegisterResponse
import kimspring.splearn.adapter.webapi.dto.MemberResponse
import kimspring.splearn.application.member.command.RegisterMemberCommand
import kimspring.splearn.application.member.command.UpdateMemberInfoCommand
import kimspring.splearn.application.member.usecase.MemberLifecycle
import kimspring.splearn.application.member.usecase.MemberModifier
import kimspring.splearn.application.member.usecase.MemberRegister
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class MemberApi(
    private val memberRegister: MemberRegister,
    private val memberLifecycle: MemberLifecycle,
    private val memberModifier: MemberModifier,
) : MemberApiSpec {
    override fun register(request: RegisterMemberCommand): ResponseEntity<MemberRegisterResponse> {
        val member = memberRegister.register(request)
        val location = URI.create("/api/members/${requireNotNull(member.id)}")
        return ResponseEntity.created(location).body(MemberRegisterResponse.of(member))
    }

    override fun activate(memberId: Long): MemberResponse = MemberResponse.of(memberLifecycle.activate(memberId))

    override fun deactivate(memberId: Long): MemberResponse = MemberResponse.of(memberLifecycle.deactivate(memberId))

    override fun updateInfo(
        memberId: Long,
        request: UpdateMemberInfoCommand,
    ): MemberResponse = MemberResponse.of(memberModifier.updateInfo(memberId, request))
}
