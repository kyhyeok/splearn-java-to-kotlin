package kimspring.learningtest.archunit

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import kimspring.splearn.support.archunit.onlyCallAllowedMethodsOfOtherSlices

// 아키텍처 규칙이 위반을 실제로 잡아내는지 검증한다 — 성공 케이스만 통과하는 테스트는 규칙이 비어 있어도 통과한다
class ArchUnitSliceLearningTest :
    FunSpec({
        val classes by lazy { ClassFileImporter().importPackages("kimspring.learningtest.archunit.slice") }

        test("학습 테스트 - 슬라이스 사이의 순환 의존을 잡아낸다") {
            shouldThrow<AssertionError> {
                slices()
                    .matching("kimspring.learningtest.archunit.slice.cycle.(*)..")
                    .should()
                    .beFreeOfCycles()
                    .check(classes)
            }
        }

        test("학습 테스트 - 다른 슬라이스의 상태 전이 메서드 호출을 잡아낸다") {
            val violation =
                shouldThrow<AssertionError> {
                    slices()
                        .matching("kimspring.learningtest.archunit.slice.mutation.(*)..")
                        .should(onlyCallAllowedMethodsOfOtherSlices())
                        .check(classes)
                }

            violation.message shouldContain "Target.activate()"
            violation.message shouldNotContain "Target.isActive()"
        }
    })
