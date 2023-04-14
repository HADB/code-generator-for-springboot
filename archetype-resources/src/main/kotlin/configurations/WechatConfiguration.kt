package ${package_name}.configurations

import com.github.binarywang.wxpay.config.WxPayConfig
import com.github.binarywang.wxpay.service.WxPayService
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnClass(WxPayService::class)
class WechatConfiguration {

    @Value("\$${wechat.weapp.app-id}")
    lateinit var weappAppId: String

    @Value("\$${wechat.weapp.app-secret}")
    lateinit var weappAppSecret: String

    @Value("\$${wechat.mch-id}")
    lateinit var mchId: String

    @Value("\$${wechat.mch-key}")
    lateinit var mchKey: String

    @Value("\$${wechat.mch-cert-path}")
    lateinit var mchCertPath: String

    @Value("\$${wechat.payment-notify-url}")
    lateinit var paymentNotifyUrl: String

    @Value("\$${wechat.refund-notify-url}")
    lateinit var refundNotifyUrl: String

    @Bean
    @ConditionalOnMissingBean
    fun wxpayService(): WxPayService {
        val payConfig = WxPayConfig()
        payConfig.appId = weappAppId
        payConfig.mchId = mchId
        payConfig.mchKey = mchKey
        payConfig.keyPath = mchCertPath
        payConfig.notifyUrl = paymentNotifyUrl
        payConfig.tradeType = "JSAPI"
        payConfig.isUseSandboxEnv = false // 可以指定是否使用沙箱环境

        val wxPayService = WxPayServiceImpl()
        wxPayService.config = payConfig
        return wxPayService
    }
}