package ${package}.configurations

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class AppConfiguration {
    @Value("\${app.qiniu.bucket}")
    lateinit var qiniuBucket: String

    @Value("\${app.qiniu.access-key}")
    lateinit var qiniuAccessKey: String

    @Value("\${app.qiniu.secret-key}")
    lateinit var qiniuSecretKey: String

    @Value("\${app.wxAppId}")
    lateinit var wxAppId: String

    @Value("\${app.wxAppSecret}")
    lateinit var wxAppSecret: String
}
