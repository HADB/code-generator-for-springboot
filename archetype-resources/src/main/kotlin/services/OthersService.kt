package ${package_name}.services

import com.qiniu.util.Auth
import ${package_name}.configurations.AppConfiguration
import org.springframework.stereotype.Component
import jakarta.annotation.Resource

@Component
class OthersService {
    @Resource
    private lateinit var appConfiguration: AppConfiguration

    fun getQiniuToken(): String {
        val bucket = appConfiguration.qiniuBucket
        val accessKey = appConfiguration.qiniuAccessKey
        val secretKey = appConfiguration.qiniuSecretKey
        val auth = Auth.create(accessKey, secretKey)
        return auth.uploadToken(bucket)
    }
}
