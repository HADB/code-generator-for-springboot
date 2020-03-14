package ${package}.others

import ${package}.constants.AppConstants

class RedisKey {
    companion object {
        const val ACCESS_TOKEN = "${AppConstants.REDIS_PREFIX}:AccessToken"

        fun token(service: String?, userId: String): String {
            return "${AppConstants.REDIS_PREFIX}:${service ?: AppConstants.Service.DEFAULT}:user-id:$userId:token"
        }

        fun userId(service: String?, token: String): String {
            return "${AppConstants.REDIS_PREFIX}:${service ?: AppConstants.Service.DEFAULT}:token:$token:user-id"
        }

        fun sessionKey(openId: String?): String {
            return "${AppConstants.REDIS_PREFIX}:OpenId:$openId:SessionKey"
        }

        fun passwordErrorTimes(mobile: String?): String {
            return "${AppConstants.REDIS_PREFIX}:Mobile:$mobile:PasswordErrorTimes"
        }
    }
}
