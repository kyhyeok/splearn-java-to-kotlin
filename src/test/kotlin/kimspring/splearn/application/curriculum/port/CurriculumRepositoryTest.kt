package kimspring.splearn.application.curriculum.port

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.curriculum.Curriculum
import kimspring.splearn.domain.curriculum.CurriculumFixture
import kimspring.splearn.domain.curriculum.SectionContent
import kimspring.splearn.domain.curriculum.lesson
import kimspring.splearn.domain.curriculum.section
import kimspring.splearn.support.test.BaseRepositoryTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.OptimisticLockingFailureException

class CurriculumRepositoryTest : BaseRepositoryTest() {
    init {
        test("saveAndFindById") {
            val saved = saveCurriculumWithContents()

            val id = saved.id.shouldNotBeNull()
            val found = curriculumRepository.findById(id).shouldNotBeNull()

            found shouldBe saved
            SectionContent.from(found) shouldContainExactly
                listOf(
                    section("S1", lesson("L1"), lesson("L2")),
                    section("S2", lesson("L3")),
                )
        }

        test("saveAssignsIdsToSectionsAndLessons") {
            val saved = saveCurriculumWithContents()

            saved.sections.forEach { it.id.shouldNotBeNull() }
            saved.allLessons().forEach { it.id.shouldNotBeNull() }
        }

        test("findByCourseId") {
            val saved = prepareCurriculum()

            curriculumRepository.findByCourseId(saved.courseId) shouldBe saved
            curriculumRepository.findByCourseId(Long.MAX_VALUE) shouldBe null
        }

        test("removeLesson") {
            val saved = saveCurriculumWithContents()

            curriculumRepository.save(saved.removeLesson(0, 0))

            SectionContent.from(reload(saved)) shouldContainExactly
                listOf(section("S1", lesson("L2")), section("S2", lesson("L3")))
        }

        test("removeSection") {
            val saved = saveCurriculumWithContents()

            curriculumRepository.save(saved.removeSection(0))

            SectionContent.from(reload(saved)) shouldContainExactly
                listOf(section("S2", lesson("L1"), lesson("L2"), lesson("L3")))
        }

        test("moveLesson") {
            val saved = saveCurriculumWithContents()

            curriculumRepository.save(saved.moveLesson(0, 0, 1, 1))

            SectionContent.from(reload(saved)) shouldContainExactly
                listOf(section("S1", lesson("L2")), section("S2", lesson("L3"), lesson("L1")))
        }

        // Spring Data JDBC 는 루트 재저장 시 자식 행을 삭제 후 재삽입하지만,
        // id 를 가진 자식은 같은 id 로 다시 들어가므로 기존 수업의 식별자가 유지되어야 한다
        test("keepsSectionAndLessonIdsAcrossResave") {
            val saved = saveCurriculumWithContents()

            curriculumRepository.save(saved.addLesson(1, "L4"))

            val reloaded = reload(saved)
            reloaded.sections.map { it.id } shouldContainExactly saved.sections.map { it.id }
            reloaded.allLessons().take(3).map { it.id } shouldContainExactly saved.allLessons().map { it.id }
        }

        // 같은 버전을 읽은 두 편집 중 나중 저장은 앞선 결과를 덮어쓰지 않고 실패해야 한다
        test("staleSaveFailsWithOptimisticLock") {
            val saved = saveCurriculumWithContents()
            val first = reload(saved)
            val second = reload(saved)

            curriculumRepository.save(first.removeSection(0))

            shouldThrow<OptimisticLockingFailureException> {
                curriculumRepository.save(second.addLesson(1, "L4"))
            }
        }

        test("uniqueCourse") {
            val saved = prepareCurriculum()
            val course = courseRepository.getById(saved.courseId)

            shouldThrow<DataIntegrityViolationException> {
                curriculumRepository.save(CurriculumFixture.createCurriculum(course))
            }
        }
    }

    private fun saveCurriculumWithContents(): Curriculum =
        curriculumRepository.save(
            prepareCurriculum()
                .addSection("S1")
                .addLesson(0, "L1")
                .addLesson(0, "L2")
                .addSection("S2")
                .addLesson(1, "L3"),
        )

    private fun reload(curriculum: Curriculum): Curriculum = curriculumRepository.getById(requireNotNull(curriculum.id))
}
