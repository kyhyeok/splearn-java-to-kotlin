package kimspring.splearn.support.stereotype

import org.springframework.web.bind.annotation.RestController

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Adapter
@RestController
annotation class WebApiAdapter
