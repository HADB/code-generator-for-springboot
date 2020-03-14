package ${package}.helpers

import ${package}.constants.AppConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ${package}.others.RedisKey
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class TokenHelper {
    @Autowired
    private lateinit var redisHelper: RedisHelper

    // 创建Token与UserId的双向映射
    fun createToken(system: String?, userId: Long): String {
        deleteToken(system, userId) // 删除老Token
        val token = "${system ?: AppConstants.Service.DEFAULT}-${UUID.randomUUID().toString().replace("-", "")}"
        redisHelper.set(RedisKey.token(system, userId.toString()), token, AppConstants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS)
        redisHelper.set(RedisKey.userId(system, token), userId.toString(), AppConstants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS)
        return token
    }

    // 根据Token获取UserId
    fun getUserIdFromToken(system: String?, token: String): Long? {
        val userId = redisHelper.get(RedisKey.userId(system, token))
        if (userId != null) {
            updateTokenUserId(system, token, userId) // 验证成功，延长 Token 的过期时间
        }
        return userId?.toLong()
    }

    // 刷新Token和UserId的双向映射
    fun updateTokenUserId(system: String?, token: String, userId: String) {
        redisHelper.set(RedisKey.token(system, userId), token, AppConstants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS)
        redisHelper.set(RedisKey.userId(system, token), userId, AppConstants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS)
    }

    // 删除token
    fun deleteToken(system: String?, userId: Long?) {
        if (userId != null) {
            val token = redisHelper.get(RedisKey.token(system, userId.toString()))
            if (token != null) {
                redisHelper.delete(RedisKey.token(system, userId.toString()))
                redisHelper.delete(RedisKey.userId(system, token))
            }
        }
    }
}
