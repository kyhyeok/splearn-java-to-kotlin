package kimspring.splearn.application.course.port

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.application.instructor.port.InstructorRepository
import kimspring.splearn.application.member.port.MemberRepository
import kimspring.splearn.domain.course.CourseFixture
import kimspring.splearn.domain.instructor.Instructor
import kimspring.splearn.domain.instructor.InstructorFixture
import kimspring.splearn.domain.member.MemberFixture
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@Transactional
class CourseRepositoryTest : FunSpec() {
    @Autowired
    private lateinit var courseRepository: CourseRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var instructorRepository: InstructorRepository

    private val now = LocalDateTime.of(2024, 1, 1, 0, 0)

    init {
        extension(SpringExtension())

        test("saveAndFindById") {
            val instructor = saveActiveInstructor()

            val saved = courseRepository.save(CourseFixture.createCourse(instructor))

            val id = saved.id.shouldNotBeNull()
            courseRepository.findById(id) shouldBe saved
        }

        test("findByTitleContaining") {
            val instructor = saveActiveInstructor()
            val hello = courseRepository.save(CourseFixture.createCourse(instructor, "Hello Spring"))
            val cleanSpring = courseRepository.save(CourseFixture.createCourse(instructor, "Clean Spring 2"))
            val cleanCode = courseRepository.save(CourseFixture.createCourse(instructor, "Clean Code"))

            courseRepository.findByTitleContaining("Spring") shouldContainExactlyInAnyOrder listOf(hello, cleanSpring)
            courseRepository.findByTitleContaining("Clean") shouldContainExactlyInAnyOrder
                listOf(cleanSpring, cleanCode)
            courseRepository.findByTitleContaining("Code") shouldContainExactlyInAnyOrder listOf(cleanCode)
            courseRepository.findByTitleContaining("JPA") shouldBe emptyList()
        }

        test("findByInstructorId") {
            val instructor = saveActiveInstructor()
            val instructor2 = saveActiveInstructor()
            val course = courseRepository.save(CourseFixture.createCourse(instructor, "Title"))
            val course2 = courseRepository.save(CourseFixture.createCourse(instructor2, "Title2"))

            courseRepository.findByInstructorId(requireNotNull(instructor.id)) shouldHaveSingleElement course
            courseRepository.findByInstructorId(requireNotNull(instructor2.id)) shouldHaveSingleElement course2
        }

        test("findByInstructorIdAndTitle") {
            val instructor = saveActiveInstructor()
            val course = courseRepository.save(CourseFixture.createCourse(instructor, "Title"))

            courseRepository.findByInstructorIdAndTitle(requireNotNull(instructor.id), "Title") shouldBe course
            courseRepository.findByInstructorIdAndTitle(requireNotNull(instructor.id), "No Such") shouldBe null
        }

        test("uniqueTitleAndInstructor") {
            val instructor = saveActiveInstructor()
            courseRepository.save(CourseFixture.createCourse(instructor, "Title"))

            shouldThrow<DataIntegrityViolationException> {
                courseRepository.save(CourseFixture.createCourse(instructor, "Title"))
            }
        }
    }

    private fun saveActiveInstructor(): Instructor {
        val member = memberRepository.save(MemberFixture.createMember())
        val activated = memberRepository.save(member.activate(now))
        return instructorRepository.save(InstructorFixture.createActiveInstructor(activated))
    }
}
