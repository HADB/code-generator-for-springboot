package ${package_name}.helpers

import ${package_name}.others.RedisKey
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

@Component
class RedisHelper {
    @Resource
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

    private fun lock(key: String, expire: Int): Boolean {
        val value = System.currentTimeMillis() + expire
        val status = redis.opsForValue().setIfAbsent(key, value.toString())!!
        if (status) {
            // logger.info("获取锁成功，key: $$key")
            return true
        }
        val oldExpireTime = redis.opsForValue().get(key)?.toLongOrNull()
        if (oldExpireTime == null || oldExpireTime < System.currentTimeMillis()) {
            val newExpireTime = System.currentTimeMillis() + expire
            val currentExpireTime = redis.opsForValue().getAndSet(key, newExpireTime.toString())?.toLongOrNull()
            if (currentExpireTime == oldExpireTime) {
                // logger.info("锁已超时，获取锁成功，key: $$key")
                return true
            }
        }
        // logger.info("获取锁失败，key: $$key")
        return false
    }

    fun lock(type: String, id: Long, name: String, expire: Int): Boolean {
        val key = RedisKey.lock(type, id.toString(), name)
        return lock(key, expire)
    }

    fun unlock(type: String, id: Long, name: String) {
        val key = RedisKey.lock(type, id.toString(), name)
        // logger.info("删除锁，key: $$key")
        redis.delete(key)
    }
}
