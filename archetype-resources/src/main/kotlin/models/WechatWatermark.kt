package ${package_name}.models

import com.fasterxml.jackson.annotation.JsonProperty


data class WechatWatermark(
    @JsonProperty("appid")
    val appId: String?,

    @JsonProperty("timestamp")
    val timestamp: String?
)