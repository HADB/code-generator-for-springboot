package ${package_name}.helpers

import ${package_name}.constants.AppConstants
import ${package_name}.others.RedisKey
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit
import jakarta.annotation.Resource

@Component
class TokenHelper {
    @Resource
    private lateinit var redisHelper: RedisHelper

    // 创建 Token 与 Key 的双向映射
    fun createToken(service: String?, key: String): String {
        deleteToken(service, key) // 删除老Token
        val token = UUID.randomUUID().toString().replace("-", "")
        redisHelper.set(RedisKey.token(service, key), token, AppConstants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS)
        redisHelper.set(RedisKey.key(service, token), key, AppConstants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS)
        return token
    }

    // 根据 Token 获取 Key
    fun getTokenKey(service: String?, token: String): String? {
        val key = redisHelper.get(RedisKey.key(service, token))
        if (key != null) {
            updateToken(service, token, key) // 验证成功，延长 Token 的过期时间
        }
        return key
    }

    // 刷新 Token 和 Key 的双向映射
    fun updateToken(service: String?, token: String, key: String) {
        redisHelper.set(RedisKey.token(service, key), token, AppConstants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS)
        redisHelper.set(RedisKey.key(service, token), key, AppConstants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS)
    }

    // 删除 Token
    fun deleteToken(service: String?, key: String?) {
        if (key != null) {
            val token = redisHelper.get(RedisKey.token(service, key))
            if (token != null) {
                redisHelper.delete(RedisKey.token(service, key))
                redisHelper.delete(RedisKey.key(service, token))
            }
        }
    }
}
