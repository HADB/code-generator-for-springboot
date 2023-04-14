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
class WxConfiguration {

    @Value("\$${wx.weapp.app-id}")
    lateinit var wxWeappAppId: String

    @Value("\$${wx.weapp.app-secret}")
    lateinit var wxWeappAppSecret: String

    @Value("\$${wx.mch-id}")
    lateinit var wxMchId: String

    @Value("\$${wx.mch-key}")
    lateinit var wxMchKey: String

    @Value("\$${wx.mch-cert-path}")
    lateinit var wxMchCertPath: String

    @Value("\$${wx.payment-notify-url}")
    lateinit var wxPaymentNotifyUrl: String

    @Value("\$${wx.refund-notify-url}")
    lateinit var wxRefundNotifyUrl: String

    @Bean
    @ConditionalOnMissingBean
    fun wxpayService(): WxPayService {
        val payConfig = WxPayConfig()
        payConfig.appId = wxWeappAppId
        payConfig.mchId = wxMchId
        payConfig.mchKey = wxMchKey
        payConfig.keyPath = wxMchCertPath
        payConfig.notifyUrl = wxPaymentNotifyUrl
        payConfig.tradeType = "JSAPI"
        payConfig.isUseSandboxEnv = false // 可以指定是否使用沙箱环境

        val wxPayService = WxPayServiceImpl()
        wxPayService.config = payConfig
        return wxPayService
    }
}