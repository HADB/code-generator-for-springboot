package ${package_name}.services

import com.qiniu.util.Auth
import net.yuanfen.op.mateo.configurations.AppConfiguration
import org.springframework.stereotype.Component
import javax.annotation.Resource

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
