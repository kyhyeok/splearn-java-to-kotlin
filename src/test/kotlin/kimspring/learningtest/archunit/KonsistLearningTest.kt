package kimspring.learningtest.archunit

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import io.kotest.core.spec.style.FunSpec

class KonsistLearningTest :
    FunSpec({
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
