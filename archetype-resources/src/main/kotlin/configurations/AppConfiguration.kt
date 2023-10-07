package ${package_name}.configurations

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
class AppConfiguration {
    lateinit var qiniuBucket: String

    lateinit var qiniuAccessKey: String

    lateinit var qiniuSecretKey: String
}
