package ${package}.viewmodels.user

data class WechatEncryptedDataRequest(
        val encryptedData: String?,
        val iv: String?
)