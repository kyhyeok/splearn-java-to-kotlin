package kimspring.splearn

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
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

                    // 비-strict dependsOn 은 "있어도 된다" 선언일 뿐 위반을 만들지 않는다.
                    // 금지 방향은 doesNotDependOn 으로 명시해야 실제로 강제된다
                    domain.dependsOnNothing()
                    application.dependsOn(domain)
                    application.doesNotDependOn(adapter)
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

        test("어댑터 슬라이스는 서로 직접 의존하지 않는다") {
            // Konsist 레이어 규칙은 같은 레이어 내부 의존을 검사하지 않으므로 ArchUnit 으로 보강한다
            slices()
                .matching("kimspring.splearn.adapter.(*)..")
                .should()
                .notDependOnEachOther()
                .check(productionClasses)
        }

        test("어댑터 루트 직속 클래스는 webapi 외 다른 어댑터 슬라이스에 의존하지 않는다") {
            // ApiControllerAdvice 처럼 adapter 루트에 놓인 클래스는 (*) 슬라이스 매칭에서 빠진다.
            // 전역 예외 처리는 웹 어댑터의 일부로 보고 webapi 의존만 허용한다
            noClasses()
                .that()
                .resideInAPackage("kimspring.splearn.adapter")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                    "kimspring.splearn.adapter.out..",
                    "kimspring.splearn.adapter.security..",
                    "kimspring.splearn.adapter.integration..",
                ).check(productionClasses)
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
