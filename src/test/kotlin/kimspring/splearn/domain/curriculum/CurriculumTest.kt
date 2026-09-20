package kimspring.splearn.domain.curriculum

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kimspring.splearn.domain.course.CourseFixture

class CurriculumTest :
    FunSpec({
        test("create") {
            val course = CourseFixture.createCourse().copy(id = 7L)

            val curriculum = Curriculum.create(course)

            curriculum.courseId shouldBe course.id
            curriculum.sections.shouldBeEmpty()
        }

        test("createFailUnsavedCourse") {
            shouldThrow<IllegalArgumentException> { Curriculum.create(CourseFixture.createCourse()) }
        }

        test("titleTooLongFail") {
            val tooLong = "a".repeat(CURRICULUM_TITLE_MAX_LENGTH + 1)

            shouldThrow<IllegalArgumentException> { Section.create(tooLong) }
            shouldThrow<IllegalArgumentException> { Lesson.create(tooLong) }
        }

        test("addSection") {
            val curriculum = CurriculumFixture.createCurriculum().addSection("Section 1")

            curriculum.sections.map { it.title } shouldContainExactly listOf("Section 1")
        }

        test("addSectionWithIndex") {
            val curriculum =
                CurriculumFixture
                    .createCurriculum()
                    .addSection("S1")
                    .addSection("S2")
                    .addSection(1, "S1_1")
                    .addSection(3, "S3")

            curriculum.sections.map { it.title } shouldContainExactly listOf("S1", "S1_1", "S2", "S3")

            shouldThrow<IndexOutOfBoundsException> { curriculum.addSection(5, "Fail") }
        }

        test("addLesson") {
            val curriculum =
                CurriculumFixture
                    .createCurriculum()
                    .addSection("S0")
                    .addSection("S1")
                    .addLesson(0, "L0")
                    .addLesson(0, "L1")
                    .addLesson(1, "L2")

            SectionContent.from(curriculum) shouldContainExactly
                listOf(
                    section("S0", lesson("L0"), lesson("L1")),
                    section("S1", lesson("L2")),
                )
        }

        test("updateSectionTitle") {
            val curriculum =
                CurriculumFixture
                    .createCurriculum()
                    .addSection("S0")
                    .addSection("S1")
                    .updateSectionTitle(0, "S0 Updated")
                    .updateSectionTitle(1, "S1 Updated")

            curriculum.sections.map { it.title } shouldContainExactly listOf("S0 Updated", "S1 Updated")
        }

        test("updateLessonTitle") {
            val curriculum =
                CurriculumFixture
                    .createCurriculum()
                    .addSection("S0")
                    .addSection("S1")
                    .addLesson(0, "L0_0")
                    .addLesson(0, "L0_1")
                    .addLesson(1, "L1")
                    .updateLessonTitle(0, 0, "L0_0 Updated")
                    .updateLessonTitle(1, 0, "L1 Updated")

            curriculum.allLessons().map { it.title } shouldContainExactly listOf("L0_0 Updated", "L0_1", "L1 Updated")
        }

        test("removeLesson") {
            val curriculum =
                CurriculumFixture
                    .createCurriculum()
                    .addSection("S0")
                    .addSection("S1")
                    .addLesson(0, "L0")
                    .addLesson(0, "L1")
                    .addLesson(1, "L2")
                    .addLesson(1, "L3")

            curriculum.allLessons().map { it.title } shouldContainExactly listOf("L0", "L1", "L2", "L3")

            val removedFirst = curriculum.removeLesson(0, 0)
            removedFirst.allLessons().map { it.title } shouldContainExactly listOf("L1", "L2", "L3")

            val removedLast = removedFirst.removeLesson(1, 1)
            removedLast.allLessons().map { it.title } shouldContainExactly listOf("L1", "L2")
        }

        test("removeSection") {
            val curriculum =
                CurriculumFixture
                    .createCurriculum()
                    .addSection("S0")
                    .addSection("S1")
                    .addSection("S2")
                    .addLesson(0, "L0")
                    .addLesson(0, "L1")
                    .addLesson(1, "L2")
                    .addLesson(1, "L3")
                    .addLesson(2, "L4")
                    .addLesson(2, "L5")

            // 섹션을 삭제하면 그 수업은 이전 섹션의 수업 뒤에 추가된다
            val removedLast = curriculum.removeSection(2)

            SectionContent.from(removedLast) shouldContainExactly
                listOf(
                    section("S0", lesson("L0"), lesson("L1")),
                    section("S1", lesson("L2"), lesson("L3"), lesson("L4"), lesson("L5")),
                )

            // 단, 첫 번째 섹션을 삭제하면 수업은 다음 섹션 앞부분에 추가된다
            val removedFirst = removedLast.removeSection(0)

            SectionContent.from(removedFirst) shouldContainExactly
                listOf(
                    section("S1", lesson("L0"), lesson("L1"), lesson("L2"), lesson("L3"), lesson("L4"), lesson("L5")),
                )

            // 하나 남은 섹션은 삭제할 수 없다
            shouldThrow<InvalidCurriculumException> { removedFirst.removeSection(0) }
        }

        test("moveLesson") {
            val curriculum =
                CurriculumFixture
                    .createCurriculum()
                    .addSection("S0")
                    .addSection("S1")
                    .addSection("S2")
                    .addLesson(0, "L0")
                    .addLesson(0, "L1")
                    .addLesson(0, "L2")
                    .addLesson(1, "L3")
                    .addLesson(1, "L4")
                    .addLesson(2, "L5")
                    .addLesson(2, "L6")

            val moved1 = curriculum.moveLesson(0, 0, 0, 1)
            SectionContent.from(moved1) shouldContainExactly
                listOf(
                    section("S0", lesson("L1"), lesson("L0"), lesson("L2")),
                    section("S1", lesson("L3"), lesson("L4")),
                    section("S2", lesson("L5"), lesson("L6")),
                )

            val moved2 = moved1.moveLesson(0, 2, 0, 0)
            SectionContent.from(moved2) shouldContainExactly
                listOf(
                    section("S0", lesson("L2"), lesson("L1"), lesson("L0")),
                    section("S1", lesson("L3"), lesson("L4")),
                    section("S2", lesson("L5"), lesson("L6")),
                )

            val moved3 = moved2.moveLesson(0, 1, 1, 2)
            SectionContent.from(moved3) shouldContainExactly
                listOf(
                    section("S0", lesson("L2"), lesson("L0")),
                    section("S1", lesson("L3"), lesson("L4"), lesson("L1")),
                    section("S2", lesson("L5"), lesson("L6")),
                )

            val moved4 = moved3.moveLesson(2, 1, 0, 1)
            SectionContent.from(moved4) shouldContainExactly
                listOf(
                    section("S0", lesson("L2"), lesson("L6"), lesson("L0")),
                    section("S1", lesson("L3"), lesson("L4"), lesson("L1")),
                    section("S2", lesson("L5")),
                )
        }

        test("moveLessonFail") {
            val curriculum =
                CurriculumFixture
                    .createCurriculum()
                    .addSection("S0")
                    .addSection("S1")
                    .addLesson(0, "L0")

            shouldThrow<IndexOutOfBoundsException> { curriculum.moveLesson(0, 1, 1, 0) }
            shouldThrow<IndexOutOfBoundsException> { curriculum.moveLesson(0, 0, 2, 0) }
            shouldThrow<IndexOutOfBoundsException> { curriculum.moveLesson(0, 0, 1, 1) }
        }

        test("validate") {
            val empty = CurriculumFixture.createCurriculum()

            // 최소한 하나의 섹션은 필요하다
            shouldThrow<InvalidCurriculumException> { empty.validate() }

            // 수업이 없는 섹션은 검증 실패
            val emptySection = empty.addSection("S0")
            shouldThrow<InvalidCurriculumException> { emptySection.validate() }

            val oneLesson = emptySection.addLesson(0, "L0")
            oneLesson.validate()

            val twoSections = oneLesson.addSection("S1").addLesson(1, "L1")
            twoSections.validate()

            shouldThrow<InvalidCurriculumException> { twoSections.removeLesson(1, 0).validate() }
        }

        test("firstLesson") {
            val empty = CurriculumFixture.createCurriculum().addSection("S0").addSection("S1")

            empty.firstLesson().shouldBeNull()

            // 빈 섹션은 건너뛴다
            val curriculum = empty.addLesson(1, "L0").addLesson(1, "L1")

            curriculum.firstLesson().shouldNotBeNull().title shouldBe "L0"
        }

        test("nextLesson") {
            val curriculum =
                CurriculumFixture
                    .createCurriculum()
                    .addSection("S0")
                    .addSection("S1")
                    .addLesson(0, "L0")
                    .addLesson(0, "L1")
                    .addLesson(1, "L2")

            val first = curriculum.firstLesson().shouldNotBeNull()
            val second = curriculum.nextLesson(first).shouldNotBeNull()
            second.title shouldBe "L1"

            // 섹션 경계를 넘어 다음 수업을 찾는다
            val third = curriculum.nextLesson(second).shouldNotBeNull()
            third.title shouldBe "L2"

            curriculum.nextLesson(third).shouldBeNull()
        }

        test("nextLessonFailNotInCurriculum") {
            val curriculum = CurriculumFixture.createCurriculum().addSection("S0").addLesson(0, "L0")

            shouldThrow<IllegalArgumentException> { curriculum.nextLesson(Lesson.create("Other")) }
        }

        test("nextLessonWithId") {
            val curriculum =
                Curriculum(
                    courseId = 1L,
                    sections =
                        listOf(
                            Section(title = "S0", lessons = listOf(Lesson(10L, "L0"), Lesson(11L, "L1"))),
                            Section(title = "S1", lessons = listOf(Lesson(12L, "L2"))),
                        ),
                )

            curriculum.nextLesson(10L).shouldNotBeNull().id shouldBe 11L
            curriculum.nextLesson(11L).shouldNotBeNull().id shouldBe 12L
            curriculum.nextLesson(12L).shouldBeNull()

            shouldThrow<LessonNotFoundException> { curriculum.nextLesson(99L) }
        }
    })
