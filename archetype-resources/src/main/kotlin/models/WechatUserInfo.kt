package ${package_name}.models

import com.fasterxml.jackson.annotation.JsonProperty

data class WechatUserInfo(
        @JsonProperty("openId")
        val openId: String?,

        @JsonProperty("nickname")
        val nickname: String?,

        @JsonProperty("gender")
        val gender: Int?,

        @JsonProperty("city")
        val city: String?,

        @JsonProperty("province")
        val province: String?,

        @JsonProperty("country")
        val country: String?,

        @JsonProperty("avatarUrl")
        val avatarUrl: String?,

        @JsonProperty("watermark")
        val watermark: WechatWatermark
)
