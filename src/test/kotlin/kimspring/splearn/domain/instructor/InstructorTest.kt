package kimspring.splearn.domain.instructor

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.member.InvalidMemberStateException
import kimspring.splearn.domain.member.MemberFixture

class InstructorTest :
    FunSpec({
        test("apply") {
            val member = MemberFixture.createActiveMember()

            val instructor = Instructor.apply(member)

            instructor.memberId shouldBe member.id
            instructor.status shouldBe InstructorStatus.PENDING
        }

        test("applyFail") {
            val member = MemberFixture.createMember(1L) // PENDING

            shouldThrow<InvalidMemberStateException> { Instructor.apply(member) }
        }

        test("approve") {
            val instructor = InstructorFixture.createInstructor()

            val approved = instructor.approve()

            approved.status shouldBe InstructorStatus.ACTIVE
        }

        test("approveFail") {
            val instructor = InstructorFixture.createActiveInstructor()

            shouldThrow<InvalidInstructorStateException> { instructor.approve() }
        }

        test("reject") {
            val instructor = InstructorFixture.createInstructor()

            val rejected = instructor.reject()

            rejected.status shouldBe InstructorStatus.REJECTED
        }

        test("rejectFail") {
            val rejected = InstructorFixture.createInstructor().reject()

            shouldThrow<InvalidInstructorStateException> { rejected.reject() }
        }

        test("isActive") {
            val instructor = InstructorFixture.createInstructor()
            instructor.isActive() shouldBe false

            instructor.approve().isActive() shouldBe true
        }

        test("ensureActive") {
            val instructor = InstructorFixture.createInstructor()

            shouldThrow<InvalidInstructorStateException> { instructor.ensureActive() }

            instructor.approve().ensureActive()
        }
    })
