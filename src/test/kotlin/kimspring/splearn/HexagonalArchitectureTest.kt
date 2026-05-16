package kimspring.splearn

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import io.kotest.core.spec.style.FunSpec

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
    })
