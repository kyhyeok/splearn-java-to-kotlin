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
        test("run") {
            mockkStatic(SpringApplication::class)
            val context = mockk<ConfigurableApplicationContext>()
            every { SpringApplication.run(SplearnApplication::class.java, *anyVararg<String>()) } returns context

            main(arrayOf())

            verify { SpringApplication.run(SplearnApplication::class.java, *anyVararg<String>()) }
            unmockkStatic(SpringApplication::class)
        }
    })
