package ${package_name}.models

import com.fasterxml.jackson.annotation.JsonProperty

data class WechatPhoneNumberInfo(
    @JsonProperty("phoneNumber")
    val phoneNumber: String?,

    @JsonProperty("purePhoneNumber")
    val purePhoneNumber: String?,

    @JsonProperty("countryCode")
    val countryCode: String?,

    @JsonProperty("watermark")
    val watermark: WechatWatermark
)