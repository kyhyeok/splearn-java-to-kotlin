package kimspring.splearn.application.enrollment.port

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.enrollment.EnrollmentFixture
import kimspring.splearn.support.test.BaseRepositoryTest
import org.springframework.dao.DataIntegrityViolationException

class EnrollmentRepositoryTest : BaseRepositoryTest() {
    init {
        test("saveAndFindById") {
            val member = prepareActiveMember()
            val course = preparePublishedCourse()

            val saved = enrollmentRepository.save(EnrollmentFixture.createEnrollment(member, course))

            val id = saved.id.shouldNotBeNull()
            enrollmentRepository.findById(id) shouldBe saved
        }

        test("findByMemberId") {
            val member1 = prepareActiveMember()
            val member2 = prepareActiveMember()
            val enrollment1a = prepareEnrollment(member1, preparePublishedCourse())
            val enrollment1b = prepareEnrollment(member1, preparePublishedCourse())
            val enrollment2 = prepareEnrollment(member2, preparePublishedCourse())

            enrollmentRepository.findByMemberId(requireNotNull(member1.id)) shouldContainExactlyInAnyOrder
                listOf(enrollment1a, enrollment1b)
            enrollmentRepository.findByMemberId(requireNotNull(member2.id)) shouldHaveSingleElement enrollment2
        }

        test("findByMemberIdAndCourseId") {
            val member1 = prepareActiveMember()
            val member2 = prepareActiveMember()
            val course1 = preparePublishedCourse()
            val course2 = preparePublishedCourse()
            val enrollment1 = prepareEnrollment(member1, course1)
            val enrollment2 = prepareEnrollment(member2, course2)

            enrollmentRepository.findByMemberIdAndCourseId(
                requireNotNull(member1.id),
                requireNotNull(course1.id),
            ) shouldBe enrollment1
            enrollmentRepository.findByMemberIdAndCourseId(
                requireNotNull(member2.id),
                requireNotNull(course2.id),
            ) shouldBe enrollment2
            enrollmentRepository.findByMemberIdAndCourseId(
                requireNotNull(member1.id),
                requireNotNull(course2.id),
            ) shouldBe null
        }

        test("uniqueMemberAndCourse") {
            val member = prepareActiveMember()
            val course = preparePublishedCourse()
            prepareEnrollment(member, course)

            shouldThrow<DataIntegrityViolationException> {
                enrollmentRepository.save(EnrollmentFixture.createEnrollment(member, course))
            }
        }
    }
}
