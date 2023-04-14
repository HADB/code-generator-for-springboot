package ${package_name}.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest
import com.github.binarywang.wxpay.constant.WxPayConstants
import com.github.binarywang.wxpay.service.WxPayService
import com.github.binarywang.wxpay.util.SignUtils
import ${package_name}.configurations.AppConfiguration
import ${package_name}.configurations.WechatConfiguration
import ${package_name}.constants.PaymentStatus
import ${package_name}.helpers.RequestHelper
import ${package_name}.mappers.PaymentMapper
import ${package_name}.models.Payment
import ${package_name}.models.Response
import ${package_name}.viewmodels.payment.PaymentEditRequest
import ${package_name}.viewmodels.payment.PaymentPartlyEditRequest
import ${package_name}.viewmodels.payment.PaymentSearchRequest
import ${package_name}.viewmodels.payment.WxPrepayResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

@Component
class PaymentService {
    @Resource
    private lateinit var paymentMapper: PaymentMapper

    @Resource
    private lateinit var objectMapper: ObjectMapper

    @Resource
    private lateinit var userService: UserService

    @Resource
    private lateinit var wxpayService: WxPayService

    @Resource
    private lateinit var wechatConfiguration: WechatConfiguration

    @Resource
    private lateinit var requestHelper: RequestHelper

    @Resource
    private lateinit var appConfiguration: AppConfiguration


    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun editPayment(request: PaymentEditRequest): Long {
        val payment = Payment(
            id = request.id,
            userId = request.userId,
            body = request.body,
            detail = request.detail,
            amount = request.amount,
            status = request.status,
            paymentType = request.paymentType,
            wxTransactionId = request.wxTransactionId,
            wxOutTradeNo = request.wxOutTradeNo,
            wxPaymentOpenId = request.wxPaymentOpenId,
            message = request.message,
            prepayTime = request.prepayTime,
            canceledTime = request.canceledTime,
            paymentTime = request.paymentTime,
            refundTime = request.refundTime
        )
        return editPayment(payment)
    }

    fun editPayment(payment: Payment): Long {
        if (payment.id == 0L) {
            paymentMapper.insertPayment(payment)
        } else {
            paymentMapper.updatePayment(payment)
        }
        return payment.id
    }

    fun editPaymentPartly(request: PaymentPartlyEditRequest) {
        paymentMapper.updatePaymentPartly(request)
    }

    fun deletePayment(id: Long) {
        paymentMapper.deletePayment(id)
    }

    fun getPaymentById(id: Long): Payment? {
        return paymentMapper.selectPaymentById(id)
    }

    fun searchPagingPayments(request: PaymentSearchRequest): List<Payment> {
        return paymentMapper.selectPagingPayments(request)
    }

    fun searchPagingPaymentsCount(request: PaymentSearchRequest): Long {
        return paymentMapper.selectPagingPaymentsCount(request)
    }

    fun getPaymentByWxOutTradeNo(wxOutTradeNo: String?): Payment? {
        if (wxOutTradeNo == null) {
            return null
        }
        return paymentMapper.selectPaymentByWxOutTradeNo(wxOutTradeNo)
    }


    fun prepay(paymentId: Long, servletRequest: HttpServletRequest): Response<WxPrepayResponse> {
        val payment = getPaymentById(paymentId) ?: return Response.error("支付订单不存在")

        when (payment.status) {
            PaymentStatus.Canceled -> return Response.error("订单已取消")
            PaymentStatus.Paid -> return Response.error("订单已支付")
            PaymentStatus.Refunded -> return Response.error("订单已退款")
        }

        val unifiedOrderResult = unifiedOrder(payment, servletRequest) ?: return Response.Errors.wechatPrepayError()
        return Response.success(unifiedOrderResult)
    }

    fun unifiedOrder(payment: Payment, servletRequest: HttpServletRequest): WxPrepayResponse? {
        val user = userService.getUserById(payment.userId)!!

        val prepayRequest = WxPayUnifiedOrderRequest()
        prepayRequest.body = payment.body
        prepayRequest.detail = payment.detail
        prepayRequest.outTradeNo = payment.wxOutTradeNo
        prepayRequest.feeType = "CNY"
        prepayRequest.totalFee = payment.amount
        prepayRequest.openid = user.openId
        prepayRequest.spbillCreateIp = requestHelper.getClientIp(servletRequest) ?: InetAddress.getLocalHost().hostAddress
        prepayRequest.deviceInfo = "MiniProgram"

        try {
            val response = wxpayService.unifiedOrder(prepayRequest)
            logger.info(objectMapper.writeValueAsString(response))
            if (response.returnCode == "SUCCESS") {
                if (response.resultCode == "SUCCESS") {
                    val prepayId = response.prepayId

                    val payResult = WxPayMpOrderResult.builder()
                        .appId(wechatConfiguration.weappAppId)
                        .timeStamp((System.currentTimeMillis() / 1000).toString())
                        .nonceStr(System.currentTimeMillis().toString())
                        .packageValue("prepay_id=$$prepayId")
                        .signType(WxPayConstants.SignType.MD5)
                        .build()

                    payResult.paySign = SignUtils.createSign(payResult, WxPayConstants.SignType.MD5, wechatConfiguration.mchKey, null)

                    payment.status = PaymentStatus.Prepaid
                    paymentMapper.updatePayment(payment)

                    return WxPrepayResponse(
                        paymentId = payment.id,
                        appId = payResult.appId,
                        timeStamp = payResult.timeStamp,
                        nonceStr = payResult.nonceStr,
                        packageValue = payResult.packageValue,
                        signType = payResult.signType,
                        paySign = payResult.paySign
                    )
                } else {
                    logger.error(objectMapper.writeValueAsString(response))
                    logger.error("prepay error, err_code_des: " + response.errCodeDes)
                    return null
                }
            } else {
                logger.error(objectMapper.writeValueAsString(response))
                logger.error("prepay error, return_msg: " + response.returnMsg)
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun handleWxpayOrderNotify(xmlData: String): String {
        var result = true
        try {
            val wxPayOrderNotifyResult = wxpayService.parseOrderNotifyResult(xmlData)
            logger.info(objectMapper.writeValueAsString(wxPayOrderNotifyResult))

            if (wxPayOrderNotifyResult.returnCode == "SUCCESS") {
                val wxOutTradeNo = wxPayOrderNotifyResult.outTradeNo
                val payment = getPaymentByWxOutTradeNo(wxOutTradeNo)
                if (payment == null) {
                    result = false
                    logger.warn("payment is null, outTradeNo:$$wxOutTradeNo")
                } else {
                    payment.wxTransactionId = wxPayOrderNotifyResult.transactionId
                    if (wxPayOrderNotifyResult.resultCode == "FAIL") {
                        payment.message = wxPayOrderNotifyResult.errCodeDes
                    } else if (wxPayOrderNotifyResult.resultCode == "SUCCESS") {
                        payment.paymentTime = Date()
                        payment.wxPaymentOpenId = wxPayOrderNotifyResult.openid
                        payment.status = PaymentStatus.Paid
                    }

                    paymentMapper.updatePayment(payment)
                    // TODO: 处理业务逻辑
                }
            }

        } catch (ex: Exception) {
            logger.error(ex.message, ex)
            result = false
        }
        if (!result) {
            return WxPayNotifyResponse.fail("失败")
        }
        return WxPayNotifyResponse.success("成功")
    }

    fun handleWxpayRefundNotify(xmlData: String): String {
        return WxPayNotifyResponse.fail("失败")
    }
}