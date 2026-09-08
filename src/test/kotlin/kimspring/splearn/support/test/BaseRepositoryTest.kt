package kimspring.splearn.support.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import kimspring.splearn.application.course.port.CourseRepository
import kimspring.splearn.application.enrollment.port.EnrollmentRepository
import kimspring.splearn.application.instructor.port.InstructorRepository
import kimspring.splearn.application.member.port.MemberRepository
import kimspring.splearn.domain.course.Course
import kimspring.splearn.domain.course.CourseFixture
import kimspring.splearn.domain.enrollment.Enrollment
import kimspring.splearn.domain.enrollment.EnrollmentFixture
import kimspring.splearn.domain.instructor.Instructor
import kimspring.splearn.domain.instructor.InstructorFixture
import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.MemberFixture
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 리포지토리 통합 테스트의 공통 준비 코드.
 * BaseApplicationServiceTest 와 같은 이유로 준비된 객체를 필드에 저장하지 않고 반환한다.
 */
@SpringBootTest
@Transactional
abstract class BaseRepositoryTest : FunSpec() {
    @Autowired
    protected lateinit var memberRepository: MemberRepository

    @Autowired
    protected lateinit var instructorRepository: InstructorRepository

    @Autowired
    protected lateinit var courseRepository: CourseRepository

    @Autowired
    protected lateinit var enrollmentRepository: EnrollmentRepository

    private val now = LocalDateTime.of(2024, 1, 1, 0, 0)

    init {
        extension(SpringExtension())
    }

    protected fun prepareActiveMember(): Member = memberRepository.save(MemberFixture.createMember().activate(now))

    protected fun prepareActiveInstructor(member: Member = prepareActiveMember()): Instructor =
        instructorRepository.save(InstructorFixture.createActiveInstructor(member))

    protected fun prepareCourse(instructor: Instructor = prepareActiveInstructor()): Course =
        courseRepository.save(CourseFixture.createCourse(instructor))

    protected fun prepareCourse(
        instructor: Instructor,
        title: String,
    ): Course = courseRepository.save(CourseFixture.createCourse(instructor, title))

    protected fun preparePublishedCourse(instructor: Instructor = prepareActiveInstructor()): Course =
        courseRepository.save(CourseFixture.createPublishedCourse(instructor))

    protected fun prepareEnrollment(
        member: Member,
        course: Course,
    ): Enrollment = enrollmentRepository.save(EnrollmentFixture.createEnrollment(member, course))
}
