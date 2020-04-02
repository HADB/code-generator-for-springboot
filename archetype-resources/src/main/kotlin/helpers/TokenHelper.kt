package ${package_name}.helpers

import ${package_name}.constants.AppConstants
import ${package_name}.others.RedisKey
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

@Component
class TokenHelper {
    @Resource
    private lateinit var redisHelper: RedisHelper

    // 创建 Token 与 Key 的双向映射
    fun createToken(system: String?, key: String): String {
        deleteToken(system, key) // 删除老Token
        val token = "$${system ?: AppConstants.Service.DEFAULT}-$${UUID.randomUUID().toString().replace("-", "")}"
        redisHelper.set(RedisKey.token(system, key), token, AppConstants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS)
        redisHelper.set(RedisKey.key(system, token), key, AppConstants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS)
        return token
    }

    // 根据 Token 获取 Key
    fun getTokenKey(system: String?, token: String): String? {
        val key = redisHelper.get(RedisKey.key(system, token))
        if (key != null) {
            updateToken(system, token, key) // 验证成功，延长 Token 的过期时间
        }
        return key
    }

    // 刷新 Token 和 Key 的双向映射
    fun updateToken(system: String?, token: String, key: String) {
        redisHelper.set(RedisKey.token(system, key), token, AppConstants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS)
        redisHelper.set(RedisKey.key(system, token), key, AppConstants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS)
    }

    // 删除 Token
    fun deleteToken(system: String?, key: String?) {
        if (key != null) {
            val token = redisHelper.get(RedisKey.token(system, key))
            if (token != null) {
                redisHelper.delete(RedisKey.token(system, key))
                redisHelper.delete(RedisKey.key(system, token))
            }
        }
    }
}
