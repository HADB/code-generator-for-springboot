package ${package_name}.viewmodels.payment

import com.fasterxml.jackson.annotation.JsonProperty

data class WxPrepayResponse(
    val paymentId: Long,
    val appId: String,
    val timeStamp: String,
    val nonceStr: String,
    @JsonProperty("package")
    val packageValue: String,
    val signType: String,
    val paySign: String
)