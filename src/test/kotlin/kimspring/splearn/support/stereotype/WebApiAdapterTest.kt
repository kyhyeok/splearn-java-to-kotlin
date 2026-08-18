package kimspring.splearn.support.stereotype

import kimspring.splearn.SplearnTestConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(SplearnTestConfiguration::class)
annotation class WebApiAdapterTest
