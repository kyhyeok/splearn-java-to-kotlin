package kimspring.splearn.application.member.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.member.MemberFixture
import kimspring.splearn.domain.member.MemberNotFoundException
import kimspring.splearn.support.stereotype.ApplicationServiceTest
import org.springframework.beans.factory.annotation.Autowired

@ApplicationServiceTest
class MemberFinderTest : FunSpec() {
    @Autowired
    private lateinit var memberFinder: MemberFinder

    @Autowired
    private lateinit var memberRegister: MemberRegister

    init {
        extension(SpringExtension())

        test("get") {
            val member = memberRegister.register(MemberFixture.createRegisterMemberCommand())

            val found = memberFinder.get(member.id!!)

            member.id shouldBe found.id
        }

        test("getFail") {
            shouldThrow<MemberNotFoundException> { memberFinder.get(9999L) }
        }
    }
}
