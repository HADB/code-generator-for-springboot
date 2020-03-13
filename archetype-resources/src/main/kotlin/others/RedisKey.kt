#set( $dollar = '$' )
package ${package}.others

import ${package}.constants.AppConstants

class RedisKey {
    companion object {
        const val ACCESS_TOKEN = "${dollar}{AppConstants.REDIS_PREFIX}:AccessToken"

        fun token(service: String?, userId: String): String {
            return "${dollar}{AppConstants.REDIS_PREFIX}:${dollar}{service ?: AppConstants.Service.DEFAULT}:user-id:${dollar}userId:token"
        }

        fun userId(service: String?, token: String): String {
            return "${dollar}{AppConstants.REDIS_PREFIX}:${dollar}{service ?: AppConstants.Service.DEFAULT}:token:${dollar}token:user-id"
        }

        fun sessionKey(openId: String?): String {
            return "${dollar}{AppConstants.REDIS_PREFIX}:OpenId:${dollar}openId:SessionKey"
        }

        fun passwordErrorTimes(mobile: String?): String {
            return "${dollar}{AppConstants.REDIS_PREFIX}:Mobile:${dollar}mobile:PasswordErrorTimes"
        }
    }
}
