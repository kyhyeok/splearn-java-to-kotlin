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

        test("find") {
            val member = memberRegister.register(MemberFixture.createRegisterMemberCommand())

            val found = memberFinder.find(member.id!!)

            member.id shouldBe found.id
        }

        test("findFail") {
            shouldThrow<MemberNotFoundException> { memberFinder.find(9999L) }
        }
    }
}
