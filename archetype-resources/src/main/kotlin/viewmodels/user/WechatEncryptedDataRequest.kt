package ${package_name}.viewmodels.user

data class WechatEncryptedDataRequest(
        val encryptedData: String?,
        val iv: String?
)