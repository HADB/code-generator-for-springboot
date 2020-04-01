package ${package_name}.configurations

import ${package_name}.others.RedisKeyExpirationListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer


@Configuration
class RedisConfiguration {
    @Value("\$${spring.redis.primary.host}")
    private lateinit var primaryHost: String

    @Value("\$${spring.redis.primary.port}")
    private var primaryPort: Int = 0

    @Value("\$${spring.redis.primary.password}")
    private lateinit var primaryPassword: String

    @Value("\$${spring.redis.primary.database}")
    private var primaryDatabase: Int = 0

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Bean
    @Primary
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val standaloneConfig = RedisStandaloneConfiguration()
        standaloneConfig.hostName = primaryHost
        standaloneConfig.port = primaryPort
        standaloneConfig.password = RedisPassword.of(primaryPassword)
        standaloneConfig.database = primaryDatabase
        return LettuceConnectionFactory(standaloneConfig)
    }

    @Bean
    @Primary
    fun redisTemplate(redisConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, String> {
        val redisTemplate = RedisTemplate<String, String>()
        redisTemplate.setConnectionFactory(redisConnectionFactory)
        redisTemplate.afterPropertiesSet()
        return redisTemplate
    }

    @Bean
    fun keyExpirationMessageListenerContainer(redisConnectionFactory: RedisConnectionFactory, redisKeyExpirationListener: RedisKeyExpirationListener): RedisMessageListenerContainer {
        val listenerContainer = RedisMessageListenerContainer()
        listenerContainer.setConnectionFactory(redisConnectionFactory)
        listenerContainer.addMessageListener(redisKeyExpirationListener, PatternTopic("__keyevent@*__:expired"))
        listenerContainer.setErrorHandler { e -> logger.error("There was an error in redis key expiration listener container", e) }
        return listenerContainer
    }
}
