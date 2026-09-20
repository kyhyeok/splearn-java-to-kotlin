package kimspring.splearn

import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext

class SplearnApplicationTest :
    FunSpec({
        // 단언이 실패해도 static mock 을 해제해야 같은 JVM 의 후속 @SpringBootTest 기동에 영향이 없다
        afterTest { unmockkStatic(SpringApplication::class) }

        test("run") {
            mockkStatic(SpringApplication::class)
            val context = mockk<ConfigurableApplicationContext>()
            every { SpringApplication.run(SplearnApplication::class.java, *anyVararg<String>()) } returns context

            main(arrayOf())

            verify { SpringApplication.run(SplearnApplication::class.java, *anyVararg<String>()) }
        }
    })
