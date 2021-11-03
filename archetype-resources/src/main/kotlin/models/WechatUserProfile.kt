package ${package_name}.models

import com.fasterxml.jackson.annotation.JsonProperty

data class WechatUserProfile(
    @JsonProperty("nickName")
    val nickname: String?,

    @JsonProperty("avatarUrl")
    val avatarUrl: String?,

    @JsonProperty("watermark")
    val watermark: WechatWatermark
)
