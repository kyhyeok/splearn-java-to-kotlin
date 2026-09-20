package kimspring.splearn.support.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import kimspring.splearn.application.course.usecase.CourseCreator
import kimspring.splearn.application.course.usecase.CoursePublisher
import kimspring.splearn.application.curriculum.usecase.CurriculumCoordinator
import kimspring.splearn.application.curriculum.usecase.CurriculumFinder
import kimspring.splearn.application.enrollment.command.EnrollCommand
import kimspring.splearn.application.enrollment.usecase.Enroller
import kimspring.splearn.application.instructor.usecase.InstructorApplication
import kimspring.splearn.application.member.usecase.MemberLifecycle
import kimspring.splearn.application.member.usecase.MemberRegister
import kimspring.splearn.domain.course.Course
import kimspring.splearn.domain.course.CourseFixture
import kimspring.splearn.domain.curriculum.Curriculum
import kimspring.splearn.domain.enrollment.Enrollment
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

    @Autowired
    protected lateinit var coursePublisher: CoursePublisher

    @Autowired
    protected lateinit var curriculumCoordinator: CurriculumCoordinator

    @Autowired
    protected lateinit var curriculumFinder: CurriculumFinder

    @Autowired
    protected lateinit var enroller: Enroller

    init {
        extension(SpringExtension())
    }

    protected fun prepareActiveMember(): Member {
        val registered = memberRegister.register(MemberFixture.createRegisterMemberCommand())
        return memberLifecycle.activate(requireNotNull(registered.activationToken))
    }

    protected fun preparePendingInstructor(): Instructor =
        instructorApplication.apply(requireNotNull(prepareActiveMember().id))

    protected fun prepareInstructor(): Instructor =
        instructorApplication.approve(requireNotNull(preparePendingInstructor().id))

    protected fun prepareCourse(): Course {
        val instructor = prepareInstructor()
        val created = courseCreator.create(CourseFixture.createCreateCourseCommand(requireNotNull(instructor.id)))
        return courseCreator.updateInfo(requireNotNull(created.id), CourseFixture.createUpdateCourseInfoCommand())
    }

    /** 강의 생성 시 함께 만들어진 빈 커리큘럼에 섹션 3개(S1·S2·S3)와 수업 5개(L1~L5)를 채운다 */
    protected fun prepareCurriculumSectionsAndLessons(course: Course): Curriculum {
        val curriculumId = requireNotNull(curriculumFinder.getByCourse(requireNotNull(course.id)).id)
        curriculumCoordinator.addSection(curriculumId, "S1")
        curriculumCoordinator.addLesson(curriculumId, 0, "L1")
        curriculumCoordinator.addLesson(curriculumId, 0, "L2")
        curriculumCoordinator.addSection(curriculumId, "S2")
        curriculumCoordinator.addLesson(curriculumId, 1, "L3")
        curriculumCoordinator.addLesson(curriculumId, 1, "L4")
        curriculumCoordinator.addSection(curriculumId, "S3")
        return curriculumCoordinator.addLesson(curriculumId, 2, "L5")
    }

    protected fun prepareCourseWithCurriculum(): Course {
        val course = prepareCourse()
        prepareCurriculumSectionsAndLessons(course)
        return course
    }

    protected fun preparePublishedCourse(): Course {
        val courseId = requireNotNull(prepareCourseWithCurriculum().id)
        coursePublisher.submitForReview(courseId)
        return coursePublisher.publish(courseId)
    }

    protected fun prepareEnrollment(): Enrollment {
        val member = prepareActiveMember()
        val course = preparePublishedCourse()
        return enroller.enroll(EnrollCommand(requireNotNull(member.id), requireNotNull(course.id)))
    }
}
