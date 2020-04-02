package ${package_name}.others

import ${package_name}.constants.AppConstants
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component

@Component
class RedisKeyExpirationListener : MessageListener {
    private val logger = LoggerFactory.getLogger(this.javaClass)


    override fun onMessage(message: Message, pattern: ByteArray?) {
        val key = String(message.body)
        logger.info("onRedisExpiredMessage: $$key")
        when {
            key.startsWith("$${AppConstants.REDIS_PREFIX}:DemoKey:") -> {
                val id = key.split(':').last().toLong()
                // TODO: 处理业务逻辑
            }
        }
    }
}
