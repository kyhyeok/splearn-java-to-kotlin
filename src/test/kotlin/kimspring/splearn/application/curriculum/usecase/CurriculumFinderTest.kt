package kimspring.splearn.application.curriculum.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.application.curriculum.port.CurriculumRepository
import kimspring.splearn.domain.curriculum.Curriculum
import kimspring.splearn.domain.curriculum.CurriculumNotFoundException
import kimspring.splearn.domain.curriculum.LessonNotFoundException
import kimspring.splearn.domain.curriculum.SectionContent
import kimspring.splearn.domain.curriculum.lesson
import kimspring.splearn.domain.curriculum.section
import kimspring.splearn.support.test.BaseApplicationServiceTest
import org.springframework.beans.factory.annotation.Autowired

class CurriculumFinderTest : BaseApplicationServiceTest() {
    @Autowired
    private lateinit var curriculumRepository: CurriculumRepository

    init {
        test("find") {
            val curriculum = saveCurriculum()

            curriculumFinder.find(requireNotNull(curriculum.id)) shouldBe curriculum
        }

        test("findFail") {
            shouldThrow<CurriculumNotFoundException> { curriculumFinder.find(Long.MAX_VALUE) }
        }

        test("findWithSections") {
            val curriculum =
                saveCurriculum(
                    section("S0", lesson("L0"), lesson("L1")),
                    section("S1", lesson("L2")),
                )

            val found = curriculumFinder.find(requireNotNull(curriculum.id))

            SectionContent.from(found) shouldContainExactly
                listOf(
                    section("S0", lesson("L0"), lesson("L1")),
                    section("S1", lesson("L2")),
                )
        }

        test("findByCourse") {
            val curriculum = saveCurriculum()

            curriculumFinder.findByCourse(curriculum.courseId).id shouldBe curriculum.id
        }

        test("findByCourseFail") {
            shouldThrow<CurriculumNotFoundException> { curriculumFinder.findByCourse(Long.MAX_VALUE) }
        }

        test("firstLesson") {
            val curriculum = saveCurriculum(section("S0"), section("S1", lesson("L0"), lesson("L1")))
            val firstLessonId =
                curriculum.sections[1]
                    .lessons
                    .first()
                    .id

            val firstLesson = curriculumFinder.firstLesson(requireNotNull(curriculum.id)).shouldNotBeNull()

            firstLesson.id shouldBe firstLessonId
            firstLesson.title shouldBe "L0"
        }

        test("firstLessonEmpty") {
            val curriculum = saveCurriculum(section("S0"), section("S1"))

            curriculumFinder.firstLesson(requireNotNull(curriculum.id)).shouldBeNull()
        }

        test("firstLessonFailWhenCurriculumDoesNotExist") {
            shouldThrow<CurriculumNotFoundException> { curriculumFinder.firstLesson(Long.MAX_VALUE) }
        }

        test("nextLesson") {
            val curriculum =
                saveCurriculum(
                    section("S0", lesson("L0"), lesson("L1")),
                    section("S1", lesson("L2"), lesson("L3")),
                )
            val curriculumId = requireNotNull(curriculum.id)
            val lessonIds = curriculum.allLessons().map { requireNotNull(it.id) }

            // 섹션 경계를 넘어 저장된 순서대로 다음 수업을 찾는다
            lessonIds.zipWithNext().forEach { (current, next) ->
                curriculumFinder.nextLesson(curriculumId, current).shouldNotBeNull().id shouldBe next
            }
            curriculumFinder.nextLesson(curriculumId, lessonIds.last()).shouldBeNull()
        }

        test("nextLessonFailWhenLessonDoesNotExist") {
            val curriculum = saveCurriculum(section("S0", lesson("L0")))

            shouldThrow<LessonNotFoundException> {
                curriculumFinder.nextLesson(requireNotNull(curriculum.id), Long.MAX_VALUE)
            }
        }

        test("nextLessonFailWhenCurriculumDoesNotExist") {
            shouldThrow<CurriculumNotFoundException> { curriculumFinder.nextLesson(Long.MAX_VALUE, Long.MAX_VALUE) }
        }
    }

    private fun saveCurriculum(vararg sectionContents: SectionContent): Curriculum {
        val course = prepareCourse()
        val curriculum =
            sectionContents.fold(curriculumFinder.findByCourse(requireNotNull(course.id))) { acc, content ->
                content.lessons.fold(acc.addSection(content.title)) { withSection, lessonContent ->
                    withSection.addLesson(withSection.sections.lastIndex, lessonContent.title)
                }
            }
        return curriculumRepository.save(curriculum)
    }
}
