package ${package_name}.models

import com.fasterxml.jackson.annotation.JsonProperty

data class WechatSessionResult(
    @JsonProperty("openid")
    val openId: String,

    @JsonProperty("session_key")
    val sessionKey: String
)