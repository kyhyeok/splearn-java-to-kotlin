package kimspring.splearn.application.instructor.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.application.member.usecase.MemberLifecycle
import kimspring.splearn.application.member.usecase.MemberRegister
import kimspring.splearn.domain.instructor.Instructor
import kimspring.splearn.domain.instructor.InstructorNotFoundException
import kimspring.splearn.domain.member.MemberFixture
import kimspring.splearn.support.stereotype.ApplicationServiceTest
import org.springframework.beans.factory.annotation.Autowired

@ApplicationServiceTest
class InstructorFinderTest : FunSpec() {
    @Autowired
    private lateinit var instructorFinder: InstructorFinder

    @Autowired
    private lateinit var instructorApplication: InstructorApplication

    @Autowired
    private lateinit var memberRegister: MemberRegister

    @Autowired
    private lateinit var memberLifecycle: MemberLifecycle

    init {
        extension(SpringExtension())

        test("find") {
            val instructor = applyInstructor()

            instructorFinder.find(instructor.id!!).id shouldBe instructor.id
        }

        test("findFail") {
            shouldThrow<InstructorNotFoundException> { instructorFinder.find(9999L) }
        }

        test("findByMember") {
            val instructor = applyInstructor()

            instructorFinder.findByMember(instructor.memberId)?.id shouldBe instructor.id

            instructorFinder.findByMember(Long.MAX_VALUE).shouldBeNull()
        }
    }

    private fun applyInstructor(): Instructor {
        val member = memberRegister.register(MemberFixture.createRegisterMemberCommand())
        memberLifecycle.activate(member.id!!)
        return instructorApplication.apply(member.id!!)
    }
}
