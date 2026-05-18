package kimspring.splearn.adapter.webapi

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.responses.ApiResponse
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openApi(): OpenAPI =
        OpenAPI().info(
            Info()
                .title("Splearn API")
                .version("0.0.1")
                .description("Splearn 내부 관리 API"),
        )

    @Bean
    fun globalResponseCustomizer(): OperationCustomizer =
        OperationCustomizer { operation, _ ->
            operation.responses
                .addApiResponse("400", ApiResponse().description("입력값 오류"))
                .addApiResponse("403", ApiResponse().description("접근 권한 없음"))
                .addApiResponse("405", ApiResponse().description("지원하지 않는 HTTP 메서드"))
                .addApiResponse("500", ApiResponse().description("서버 오류"))
            operation
        }
}
