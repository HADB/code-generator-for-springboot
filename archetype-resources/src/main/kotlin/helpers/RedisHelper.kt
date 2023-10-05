package ${package_name}.helpers

import ${package_name}.others.RedisKey
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit
import jakarta.annotation.Resource

@Component
class RedisHelper {
    @Resource
    private lateinit var redis: StringRedisTemplate

    fun set(key: String, value: String) {
        redis.opsForValue().set(key, value)
    }

    fun set(key: String, value: String, time: Long, timeUnit: TimeUnit) {
        redis.opsForValue().set(key, value, time, timeUnit)
    }

    fun get(key: String): String? {
        return redis.opsForValue().get(key)
    }

    fun delete(key: String) {
        redis.delete(key)
    }

    fun getExpire(key: String, timeUnit: TimeUnit): Long? {
        return redis.opsForValue().operations.getExpire(key, timeUnit)
    }

    fun keys(pattern: String): MutableSet<String> {
        return redis.keys(pattern)
    }

    fun hgetAll(key: String): Map<String, String> {
        return redis.opsForHash<String, String>().entries(key)
    }

    fun hget(key: String, field: String): String? {
        return redis.opsForHash<String, String>().get(key, field)
    }

    fun hmsetWithExpire(key: String, hash: Map<String, String>, timeout: Long, unit: TimeUnit) {
        redis.opsForHash<String, String>().putAll(key, hash)
        redis.expire(key, timeout, unit)
    }

    private fun lock(key: String, duration: Duration): Boolean {
        val value = System.currentTimeMillis() + duration.toMillis()
        val status = redis.opsForValue().setIfAbsent(key, value.toString())!!
        if (status) {
            // 获取锁成功
            return true
        }
        val oldExpireTime = redis.opsForValue().get(key)?.toLongOrNull()
        if (oldExpireTime == null || oldExpireTime < System.currentTimeMillis()) {
            val newExpireTime = System.currentTimeMillis() + duration.toMillis()
            val currentExpireTime = redis.opsForValue().getAndSet(key, newExpireTime.toString())?.toLongOrNull()
            if (currentExpireTime == oldExpireTime) {
                // 锁已超时，获取锁成功
                return true
            }
        }
        // 获取锁失败
        return false
    }

    fun lock(type: String, id: Any, name: String, duration: Duration): Boolean {
        val key = RedisKey.lock(type, id.toString(), name)
        return lock(key, duration)
    }

    fun unlock(type: String, id: Any, name: String) {
        val key = RedisKey.lock(type, id.toString(), name)
        redis.delete(key)
    }
}
