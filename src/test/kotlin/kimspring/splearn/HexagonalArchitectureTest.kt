package kimspring.splearn

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import io.kotest.core.spec.style.FunSpec
import kimspring.splearn.support.archunit.onlyCallAllowedMethodsOfOtherSlices
import kimspring.splearn.support.archunit.onlyCallReadOnlyMethodsOfClassesIn

class HexagonalArchitectureTest :
    FunSpec({
        test("헥사고날 아키텍처 레이어 의존성 규칙을 준수한다") {
            Konsist
                .scopeFromProduction()
                .assertArchitecture {
                    val domain = Layer("domain", "kimspring.splearn.domain..")
                    val application = Layer("application", "kimspring.splearn.application..")
                    val adapter = Layer("adapter", "kimspring.splearn.adapter..")

                    domain.dependsOnNothing()
                    application.dependsOn(domain)
                    adapter.dependsOn(domain, application)
                }
        }

        val productionClasses by lazy {
            ClassFileImporter()
                .withImportOption(ImportOption.DoNotIncludeTests())
                .importPackages("kimspring.splearn")
        }

        test("도메인 애그리거트 슬라이스는 순환 의존이 없다") {
            slices()
                .matching("kimspring.splearn.domain.(*)..")
                .should()
                .beFreeOfCycles()
                .check(productionClasses)
        }

        test("애플리케이션 서비스 슬라이스는 순환 의존이 없다") {
            slices()
                .matching("kimspring.splearn.application.(*)..")
                .should()
                .beFreeOfCycles()
                .check(productionClasses)
        }

        test("애그리거트는 다른 슬라이스의 조회 메서드만 호출한다") {
            slices()
                .matching("kimspring.splearn.domain.(*)..")
                .should(onlyCallAllowedMethodsOfOtherSlices())
                .check(productionClasses)
        }

        test("어댑터는 도메인 객체의 조회 메서드만 호출한다") {
            classes()
                .that()
                .resideInAPackage("kimspring.splearn.adapter..")
                .should(onlyCallReadOnlyMethodsOfClassesIn("kimspring.splearn.domain.."))
                .check(productionClasses)
        }
    })
