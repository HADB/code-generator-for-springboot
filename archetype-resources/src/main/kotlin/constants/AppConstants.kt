package ${package_name}.constants

class AppConstants {
    companion object {
        const val TOKEN = "token"
        const val SERVICE = "service"
        const val REDIS_PREFIX = "${artifact_id}"
        const val TOKEN_EXPIRES_HOUR = 24L
        const val AUTHENTICATION = "authentication"
        const val KEY = "key"
    }

    object Service {
        const val DEFAULT = "default"
        const val WXAPP_C = "wxapp_c"
    }
}
