package ${package_name}.models

import com.fasterxml.jackson.annotation.JsonProperty

data class WechatAccessTokenResult(
        @JsonProperty("access_token")
        val accessToken: String,

        @JsonProperty("expires_in")
        val expiresIn: Long
)