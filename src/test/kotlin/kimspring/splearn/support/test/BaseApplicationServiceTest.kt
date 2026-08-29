package kimspring.splearn.support.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import kimspring.splearn.application.course.usecase.CourseCreator
import kimspring.splearn.application.instructor.usecase.InstructorApplication
import kimspring.splearn.application.member.usecase.MemberLifecycle
import kimspring.splearn.application.member.usecase.MemberRegister
import kimspring.splearn.domain.course.Course
import kimspring.splearn.domain.course.CourseFixture
import kimspring.splearn.domain.instructor.Instructor
import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberFixture
import kimspring.splearn.support.stereotype.ApplicationServiceTest
import org.springframework.beans.factory.annotation.Autowired

/**
 * 유스케이스 통합 테스트의 공통 준비 코드.
 *
 * Java 원본과 달리 준비된 객체를 필드에 저장하지 않고 반환한다 —
 * 도메인이 불변 data class라 제자리 변경이 불가능하고,
 * Kotest는 스펙 인스턴스를 테스트끼리 공유하기 때문이다.
 */
@ApplicationServiceTest
abstract class BaseApplicationServiceTest : FunSpec() {
    @Autowired
    protected lateinit var memberRegister: MemberRegister

    @Autowired
    protected lateinit var memberLifecycle: MemberLifecycle

    @Autowired
    protected lateinit var instructorApplication: InstructorApplication

    @Autowired
    protected lateinit var courseCreator: CourseCreator

    init {
        extension(SpringExtension())
    }

    protected fun prepareMember(): Member {
        val registered = memberRegister.register(MemberFixture.createRegisterMemberCommand())
        return memberLifecycle.activate(requireNotNull(registered.id))
    }

    protected fun preparePendingInstructor(): Instructor =
        instructorApplication.apply(requireNotNull(prepareMember().id))

    protected fun prepareInstructor(): Instructor =
        instructorApplication.approve(requireNotNull(preparePendingInstructor().id))

    protected fun prepareCourse(): Course {
        val instructor = prepareInstructor()
        val created = courseCreator.create(CourseFixture.createCreateCourseCommand(requireNotNull(instructor.id)))
        return courseCreator.updateInfo(requireNotNull(created.id), CourseFixture.createUpdateCourseInfoCommand())
    }
}
