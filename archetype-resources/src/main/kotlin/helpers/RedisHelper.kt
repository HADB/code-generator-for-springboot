package ${package}.helpers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisHelper {
    @Autowired
    lateinit var redis: StringRedisTemplate

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
}
