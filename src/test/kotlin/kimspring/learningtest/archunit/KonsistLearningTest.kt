package kimspring.learningtest.archunit

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.core.exception.KoAssertionFailedException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class KonsistLearningTest :
    FunSpec({
        // MyAdapter → MyService2 의존이 실제로 존재한다. 비-strict dependsOn 은 이를 위반으로 보지 않고,
        // doesNotDependOn 만 실패한다는 것을 증명한다 — 성공 케이스만 있으면 규칙이 비어 있어도 통과한다
        test("학습 테스트 - doesNotDependOn 은 실제 의존이 있으면 실패한다") {
            shouldThrow<KoAssertionFailedException> {
                Konsist
                    .scopeFromTest()
                    .assertArchitecture {
                        val application = Layer("application", "kimspring.learningtest.archunit.application..")
                        val adapter = Layer("adapter", "kimspring.learningtest.archunit.adapter..")

                        adapter.doesNotDependOn(application)
                    }
            }
        }

        test("학습 테스트 - 헥사고날 아키텍처 레이어 의존성 규칙") {
            Konsist
                .scopeFromTest()
                .assertArchitecture {
                    val domain = Layer("domain", "kimspring.learningtest.archunit.domain..")
                    val application = Layer("application", "kimspring.learningtest.archunit.application..")
                    val adapter = Layer("adapter", "kimspring.learningtest.archunit.adapter..")

                    domain.dependsOnNothing()
                    application.dependsOn(domain)
                    adapter.dependsOn(domain, application)
                }
        }
    })
