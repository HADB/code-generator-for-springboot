package ${package_name}.helpers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.base.Joiner
import ${package_name}.configurations.AppConfiguration
import ${package_name}.configurations.WxConfiguration
import ${package_name}.constants.WechatConstants
import ${package_name}.models.WechatAccessTokenResult
import ${package_name}.models.WechatPhoneNumberInfo
import ${package_name}.models.WechatSessionResult
import ${package_name}.models.WechatUserProfile
import ${package_name}.others.PKCS7Encoder
import ${package_name}.others.RedisKey
import org.apache.commons.codec.binary.Base64
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.AlgorithmParameters
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.Resource
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class WechatHelper {
    @Resource
    private lateinit var objectMapper: ObjectMapper

    @Resource
    private lateinit var appConfiguration: AppConfiguration

    @Resource
    private lateinit var wxConfiguration: WxConfiguration

    @Resource
    private lateinit var redisHelper: RedisHelper

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private fun decrypt(sessionKey: String, encryptedData: String?, iv: String?): String {
        if (encryptedData == null || iv == null) {
            logger.error(("encryptedData 或 iv 为空"))
            throw RuntimeException("encryptedData 或 iv 为空")
        }
        try {
            val params = AlgorithmParameters.getInstance("AES")
            params.init(IvParameterSpec(Base64.decodeBase64(iv)))

            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(Base64.decodeBase64(sessionKey), "AES"), params)

            return String(PKCS7Encoder.decode(cipher.doFinal(Base64.decodeBase64(encryptedData))), StandardCharsets.UTF_8)
        } catch (e: Exception) {
            logger.error("AES解密失败", e)
            throw RuntimeException("AES解密失败", e)
        }
    }

    fun decryptUserProfile(sessionKey: String, encryptedData: String?, iv: String?): WechatUserProfile {
        val decryptedString = decrypt(sessionKey, encryptedData, iv)
        return objectMapper.readValue(decryptedString)
    }

    fun decryptPhoneNumber(sessionKey: String, encryptedData: String?, iv: String?): String {
        val decryptedString = decrypt(sessionKey, encryptedData, iv)
        return objectMapper.readValue<WechatPhoneNumberInfo>(decryptedString).phoneNumber!!
    }

    fun getSessionResultByCode(code: String, appId: String, appSecret: String): WechatSessionResult {
        val params = HashMap<String, String>()
        params["appid"] = appId
        params["secret"] = appSecret
        params["js_code"] = code
        params["grant_type"] = "authorization_code"

        val queryParams = Joiner.on("&").withKeyValueSeparator("=").join(params)
        val httpClient = HttpClients.createDefault()
        val httpGet = HttpGet(WechatConstants.GET_SESSION_FROM_CODE + "?" + queryParams)
        val httpResponse = httpClient.execute(httpGet)
        val entity = httpResponse.entity
        val responseString = EntityUtils.toString(entity, "UTF-8")
        logger.debug(responseString)
        return objectMapper.readValue(responseString)
    }

    fun getAccessToken(): String {
        val redisValue = redisHelper.get(RedisKey.ACCESS_TOKEN)
        if (redisValue != null) {
            return redisValue
        } else {
            val params = HashMap<String, String>()
            params["appid"] = wxConfiguration.wxAppId
            params["secret"] = wxConfiguration.wxAppSecret
            params["grant_type"] = "client_credential"

            val queryParams = Joiner.on("&").withKeyValueSeparator("=").join(params)
            val httpClient = HttpClients.createDefault()
            val httpGet = HttpGet(WechatConstants.GET_ACCESS_TOKEN + "?" + queryParams)
            val httpResponse = httpClient.execute(httpGet)
            val entity = httpResponse.entity
            val responseString = EntityUtils.toString(entity, "UTF-8")
            logger.debug(responseString)
            val result = objectMapper.readValue<WechatAccessTokenResult>(responseString)
            redisHelper.set(RedisKey.ACCESS_TOKEN, result.accessToken, result.expiresIn, TimeUnit.SECONDS)
            return result.accessToken
        }
    }

    fun sendMessage(openId: String, templateId: String, formId: String, data: Any, page: String? = null) {
        val params = mapOf(
            "touser" to openId,
            "template_id" to templateId,
            "form_id" to formId,
            "data" to data,
            "page" to page
        )
        try {
            val httpClient = HttpClients.createDefault()
            val httpPost = HttpPost(WechatConstants.SEND_TEMPLATE_MESSAGE + "?access_token=" + getAccessToken())
            val json = objectMapper.writeValueAsString(params)
            logger.info(json)
            val stringEntity = StringEntity(json, "UTF-8")
            stringEntity.setContentType("application/json")
            httpPost.entity = stringEntity
            val response = httpClient.execute(httpPost)

            response.use {
                if (it.statusLine.statusCode == 200) {
                    val entity = it.entity
                    val entityString = EntityUtils.toString(entity)
                    logger.info(entityString)
                    EntityUtils.consume(entity)
                    return objectMapper.readValue(entityString)
                } else {
                    logger.info("error：" + EntityUtils.toString(it.entity))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
