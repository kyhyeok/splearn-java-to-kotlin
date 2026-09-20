package kimspring.splearn.adapter.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.jwt.JwtClaimValidator
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import java.util.Base64
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebSecurity
class SecurityConfig(
    @param:Value("\${jwt.secret}") private val secret: String,
    @param:Value("\${jwt.issuer}") private val issuer: String,
    @param:Value("\${jwt.audience}") private val audience: String,
) {
    @Bean
    fun filterChain(
        http: HttpSecurity,
        jwtDecoder: JwtDecoder,
    ): SecurityFilterChain {
        http {
            csrf { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            authorizeHttpRequests { authorize(anyRequest, permitAll) }
            oauth2ResourceServer { jwt { this.jwtDecoder = jwtDecoder } }
        }
        return http.build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val keyBytes = Base64.getDecoder().decode(secret)
        val secretKey = SecretKeySpec(keyBytes, "HmacSHA256")
        val decoder =
            NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build()
        // 기본 검증기는 만료만 본다. 같은 키로 서명된 다른 용도의 토큰을 막으려면 iss·aud 를 함께 검증해야 한다
        decoder.setJwtValidator(
            DelegatingOAuth2TokenValidator(
                JwtValidators.createDefaultWithIssuer(issuer),
                JwtClaimValidator<Collection<String>?>(JwtClaimNames.AUD) { aud -> aud != null && audience in aud },
            ),
        )
        return decoder
    }
}
