package ${package_name}.others

import ${package_name}.constants.AppConstants

class RedisKey {
    companion object {
        const val ACCESS_TOKEN = "$${AppConstants.REDIS_PREFIX}:AccessToken"

        fun token(service: String?, key: String): String {
            return "$${AppConstants.REDIS_PREFIX}:$${service ?: AppConstants.Service.DEFAULT}:key:$$key:token"
        }

        fun key(service: String?, token: String): String {
            return "$${AppConstants.REDIS_PREFIX}:$${service ?: AppConstants.Service.DEFAULT}:token:$$token:key"
        }

        fun sessionKey(openId: String?): String {
            return "$${AppConstants.REDIS_PREFIX}:open-id:$$openId:session-key"
        }

        fun passwordErrorTimes(mobile: String?): String {
            return "$${AppConstants.REDIS_PREFIX}:mobile:$$mobile:password-error-times"
        }

        fun lock(type: String, id: String, name: String): String {
            return "$${AppConstants.REDIS_PREFIX}:lock:$$type:$$id:$$name"
        }

    }
}
