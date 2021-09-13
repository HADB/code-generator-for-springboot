package ${package_name}.others

import ${package_name}.constants.AppConstants

class RedisKey {
    companion object {
        const val ACCESS_TOKEN = "$${AppConstants.REDIS_PREFIX}:access-token"

        fun build(key: String): String {
            return "$${AppConstants.REDIS_PREFIX}:$$key".toLowerCase()
        }

        fun token(service: String?, key: String): String {
            return build("$${service ?: AppConstants.Service.DEFAULT}:key:$$key:token")
        }

        fun key(service: String?, token: String): String {
            return build("$${service ?: AppConstants.Service.DEFAULT}:token:$$token:key")
        }

        fun sessionKey(openId: String?): String {
            return build("open-id:$$openId:session-key")
        }

        fun passwordErrorTimes(mobile: String?): String {
            return build("mobile:$$mobile:password-error-times")
        }

        fun lock(type: String, id: String, name: String): String {
            return build("lock:$$type:$$id:$$name")
        }
    }
}
