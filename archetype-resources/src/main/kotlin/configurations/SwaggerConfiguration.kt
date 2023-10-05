package ${package_name}.configurations

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@SecurityScheme(name = "Token", type = SecuritySchemeType.APIKEY, scheme = "token", `in` = SecuritySchemeIn.HEADER)
class SwaggerConfiguration {

    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("${artifact_id}")
                    .description("${description} API")
                    .version("v${version}")
            )
            .security(listOf(SecurityRequirement().addList("Token")))
    }
}