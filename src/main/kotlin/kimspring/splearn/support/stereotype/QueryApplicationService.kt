package kimspring.splearn.support.stereotype

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Service
@Transactional(readOnly = true)
@Validated
annotation class QueryApplicationService
