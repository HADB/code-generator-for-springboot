package ${package_name}.services

import com.qiniu.util.Auth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ${package_name}.configurations.AppConfiguration

@Component
class OthersService {
    @Autowired
    private lateinit var appConfiguration: AppConfiguration

    fun getQiniuToken(): String {
        val bucket = appConfiguration.qiniuBucket
        val accessKey = appConfiguration.qiniuAccessKey
        val secretKey = appConfiguration.qiniuSecretKey
        val auth = Auth.create(accessKey, secretKey)
        return auth.uploadToken(bucket)
    }
}
