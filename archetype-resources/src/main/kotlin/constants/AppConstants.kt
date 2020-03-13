package ${package}.constants

class AppConstants {
    companion object {
        const val TOKEN = "token"
        const val SERVICE = "service"
        const val REDIS_PREFIX = "op:${artifactId}"
        const val TOKEN_EXPIRES_HOUR = 24L
        const val AUTHENTICATION = "authentication"
        const val USER_ID = "user_id"
    }

    object Service {
        const val DEFAULT = "default"
    }
}
