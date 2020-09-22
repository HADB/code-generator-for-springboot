package ${package_name}.configurations

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfiguration {
    @Value("\$${app.qiniu.bucket}")
    lateinit var qiniuBucket: String

    @Value("\$${app.qiniu.access-key}")
    lateinit var qiniuAccessKey: String

    @Value("\$${app.qiniu.secret-key}")
    lateinit var qiniuSecretKey: String
}
