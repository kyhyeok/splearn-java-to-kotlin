package kimspring.splearn.application.curriculum.usecase

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import kimspring.splearn.application.curriculum.port.CurriculumRepository
import kimspring.splearn.domain.curriculum.CurriculumNotFoundException
import kimspring.splearn.domain.curriculum.InvalidCurriculumException
import kimspring.splearn.domain.curriculum.SectionContent
import kimspring.splearn.domain.curriculum.lesson
import kimspring.splearn.domain.curriculum.section
import kimspring.splearn.support.test.BaseApplicationServiceTest
import org.springframework.beans.factory.annotation.Autowired

class CurriculumCoordinatorTest : BaseApplicationServiceTest() {
    @Autowired
    private lateinit var curriculumRepository: CurriculumRepository

    init {
        test("addSection") {
            val curriculumId = saveCurriculum()

            curriculumCoordinator.addSection(curriculumId, "S0")
            curriculumCoordinator.addSection(curriculumId, "S1")

            sectionContentsAfterReload(curriculumId) shouldContainExactly listOf(section("S0"), section("S1"))
        }

        test("addSectionWithIndex") {
            val curriculumId = saveCurriculum(section("S1"), section("S3"))

            curriculumCoordinator.addSection(curriculumId, 1, "S2")
            curriculumCoordinator.addSection(curriculumId, 0, "S0")
            curriculumCoordinator.addSection(curriculumId, 4, "S4")

            sectionContentsAfterReload(curriculumId) shouldContainExactly
                listOf(section("S0"), section("S1"), section("S2"), section("S3"), section("S4"))
        }

        test("addSectionFail") {
            val curriculumId = saveCurriculum(section("S0"))

            shouldThrow<IndexOutOfBoundsException> { curriculumCoordinator.addSection(curriculumId, -1, "Fail") }
            shouldThrow<IndexOutOfBoundsException> { curriculumCoordinator.addSection(curriculumId, 2, "Fail") }
        }

        test("addLesson") {
            val curriculumId = saveCurriculum(section("S0"), section("S1"))

            curriculumCoordinator.addLesson(curriculumId, 0, "L0")
            curriculumCoordinator.addLesson(curriculumId, 1, "L1")

            sectionContentsAfterReload(curriculumId) shouldContainExactly
                listOf(section("S0", lesson("L0")), section("S1", lesson("L1")))
        }

        test("addLessonFail") {
            val curriculumId = saveCurriculum(section("S0"))

            shouldThrow<IndexOutOfBoundsException> { curriculumCoordinator.addLesson(curriculumId, -1, "Fail") }
            shouldThrow<IndexOutOfBoundsException> { curriculumCoordinator.addLesson(curriculumId, 1, "Fail") }
        }

        test("updateSectionTitle") {
            val curriculumId = saveCurriculum(section("S0"), section("S1"))

            curriculumCoordinator.updateSectionTitle(curriculumId, 0, "S0 Updated")
            curriculumCoordinator.updateSectionTitle(curriculumId, 1, "S1 Updated")

            sectionContentsAfterReload(curriculumId) shouldContainExactly
                listOf(section("S0 Updated"), section("S1 Updated"))
        }

        test("updateSectionTitleFail") {
            val curriculumId = saveCurriculum(section("S0"))

            shouldThrow<IndexOutOfBoundsException> {
                curriculumCoordinator.updateSectionTitle(curriculumId, -1, "Fail")
            }
            shouldThrow<IndexOutOfBoundsException> {
                curriculumCoordinator.updateSectionTitle(curriculumId, 1, "Fail")
            }
        }

        test("updateLessonTitle") {
            val curriculumId =
                saveCurriculum(
                    section("S0", lesson("L0"), lesson("L1")),
                    section("S1", lesson("L2"), lesson("L3")),
                )

            curriculumCoordinator.updateLessonTitle(curriculumId, 0, 0, "L0 Updated")
            curriculumCoordinator.updateLessonTitle(curriculumId, 1, 1, "L3 Updated")

            sectionContentsAfterReload(curriculumId) shouldContainExactly
                listOf(
                    section("S0", lesson("L0 Updated"), lesson("L1")),
                    section("S1", lesson("L2"), lesson("L3 Updated")),
                )
        }

        test("updateLessonTitleFail") {
            val curriculumId = saveCurriculum(section("S0", lesson("L0")))

            shouldThrow<IndexOutOfBoundsException> {
                curriculumCoordinator.updateLessonTitle(curriculumId, -1, 0, "Fail")
            }
            shouldThrow<IndexOutOfBoundsException> {
                curriculumCoordinator.updateLessonTitle(curriculumId, 0, 1, "Fail")
            }
        }

        test("removeLesson") {
            val curriculumId =
                saveCurriculum(
                    section("S0", lesson("L0"), lesson("L1")),
                    section("S1", lesson("L2"), lesson("L3")),
                )

            curriculumCoordinator.removeLesson(curriculumId, 0, 0)
            curriculumCoordinator.removeLesson(curriculumId, 1, 1)

            sectionContentsAfterReload(curriculumId) shouldContainExactly
                listOf(section("S0", lesson("L1")), section("S1", lesson("L2")))
        }

        test("removeLessonFail") {
            val curriculumId = saveCurriculum(section("S0", lesson("L0")))

            shouldThrow<IndexOutOfBoundsException> { curriculumCoordinator.removeLesson(curriculumId, -1, 0) }
            shouldThrow<IndexOutOfBoundsException> { curriculumCoordinator.removeLesson(curriculumId, 0, 1) }
        }

        test("removeFirstSection") {
            val curriculumId =
                saveCurriculum(
                    section("S0", lesson("L0"), lesson("L1")),
                    section("S1", lesson("L2")),
                )

            curriculumCoordinator.removeSection(curriculumId, 0)

            sectionContentsAfterReload(curriculumId) shouldContainExactly
                listOf(section("S1", lesson("L0"), lesson("L1"), lesson("L2")))
        }

        test("removeLastSection") {
            val curriculumId =
                saveCurriculum(
                    section("S0", lesson("L0")),
                    section("S1", lesson("L1"), lesson("L2")),
                )

            curriculumCoordinator.removeSection(curriculumId, 1)

            sectionContentsAfterReload(curriculumId) shouldContainExactly
                listOf(section("S0", lesson("L0"), lesson("L1"), lesson("L2")))
        }

        test("removeSectionFail") {
            val withOneSection = saveCurriculum(section("S0", lesson("L0")))

            shouldThrow<InvalidCurriculumException> { curriculumCoordinator.removeSection(withOneSection, 0) }

            val curriculumId = saveCurriculum(section("S0"), section("S1"))

            shouldThrow<IndexOutOfBoundsException> { curriculumCoordinator.removeSection(curriculumId, -1) }
            shouldThrow<IndexOutOfBoundsException> { curriculumCoordinator.removeSection(curriculumId, 2) }
        }

        test("moveLessonInSameSection") {
            val curriculumId = saveCurriculum(section("S0", lesson("L0"), lesson("L1"), lesson("L2")))

            curriculumCoordinator.moveLesson(curriculumId, 0, 0, 0, 1)

            sectionContentsAfterReload(curriculumId) shouldContainExactly
                listOf(section("S0", lesson("L1"), lesson("L0"), lesson("L2")))
        }

        test("moveLessonBetweenSections") {
            val curriculumId =
                saveCurriculum(
                    section("S0", lesson("L0"), lesson("L1")),
                    section("S1", lesson("L2")),
                )

            curriculumCoordinator.moveLesson(curriculumId, 0, 1, 1, 0)

            sectionContentsAfterReload(curriculumId) shouldContainExactly
                listOf(section("S0", lesson("L0")), section("S1", lesson("L1"), lesson("L2")))
        }

        test("moveLessonFail") {
            val curriculumId = saveCurriculum(section("S0", lesson("L0")), section("S1"))

            shouldThrow<IndexOutOfBoundsException> { curriculumCoordinator.moveLesson(curriculumId, -1, 0, 1, 0) }
            shouldThrow<IndexOutOfBoundsException> { curriculumCoordinator.moveLesson(curriculumId, 0, 1, 1, 0) }
            shouldThrow<IndexOutOfBoundsException> { curriculumCoordinator.moveLesson(curriculumId, 0, 0, 2, 0) }
            shouldThrow<IndexOutOfBoundsException> { curriculumCoordinator.moveLesson(curriculumId, 0, 0, 1, 1) }
        }

        test("validate") {
            val courseId = requireNotNull(prepareCourse().id)
            val curriculumId = requireNotNull(curriculumFinder.findByCourse(courseId).id)
            curriculumCoordinator.addSection(curriculumId, "S0")
            curriculumCoordinator.addLesson(curriculumId, 0, "L0")

            // validate()는 강의(Course) ID를 받아 검증하고, 문제가 없으면 예외를 던지지 않는다
            shouldNotThrowAny { curriculumCoordinator.validate(courseId) }
        }

        test("validateFail") {
            val courseIdWithoutSection = requireNotNull(prepareCourse().id)

            val courseIdWithEmptySection = requireNotNull(prepareCourse().id)
            val curriculumId = requireNotNull(curriculumFinder.findByCourse(courseIdWithEmptySection).id)
            curriculumCoordinator.addSection(curriculumId, "S0")
            curriculumCoordinator.addLesson(curriculumId, 0, "L0")
            curriculumCoordinator.addSection(curriculumId, "S1")

            shouldThrow<InvalidCurriculumException> { curriculumCoordinator.validate(courseIdWithoutSection) }
            shouldThrow<InvalidCurriculumException> { curriculumCoordinator.validate(courseIdWithEmptySection) }
        }

        test("modifyFailWhenCurriculumDoesNotExist") {
            shouldThrow<CurriculumNotFoundException> { curriculumCoordinator.addSection(Long.MAX_VALUE, "Fail") }
        }
    }

    /** 강의 생성 시 함께 만들어진 빈 커리큘럼에 기대 구성대로 섹션·수업을 채워 저장하고 id 를 반환한다 */
    private fun saveCurriculum(vararg sectionContents: SectionContent): Long {
        val course = prepareCourse()
        val curriculum =
            sectionContents.fold(curriculumFinder.findByCourse(requireNotNull(course.id))) { acc, content ->
                content.lessons.fold(acc.addSection(content.title)) { withSection, lessonContent ->
                    withSection.addLesson(withSection.sections.lastIndex, lessonContent.title)
                }
            }
        return requireNotNull(curriculumRepository.save(curriculum).id)
    }

    private fun sectionContentsAfterReload(curriculumId: Long): List<SectionContent> =
        SectionContent.from(curriculumFinder.find(curriculumId))
}
