package ${package_name}.controllers

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Operation
import ${package_name}.annotations.AllowAnonymous
import ${package_name}.annotations.BuiltInRole
import ${package_name}.constants.BuiltInRoleKey
import ${package_name}.models.Response
import ${package_name}.models.Payment
import ${package_name}.services.PaymentService
import ${package_name}.viewmodels.common.SearchResponse
import ${package_name}.viewmodels.payment.PaymentSearchRequest
import ${package_name}.viewmodels.payment.WxPrepayResponse
import org.springframework.web.bind.annotation.*
import jakarta.annotation.Resource
import jakarta.servlet.http.HttpServletRequest

@Tag(name = "支付")
@CrossOrigin
@RestController
@RequestMapping("/payment")
class PaymentController {
    @Resource
    private lateinit var paymentService: PaymentService

    @Operation(summary = "删除「支付」")
    @Parameter(name = "id", description = "Payment ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.DELETE])
    @BuiltInRole([BuiltInRoleKey.Admin])
    fun delete(@PathVariable("id") id: Long): Response<Any> {
        paymentService.deletePayment(id)
        return Response.success()
    }

    @Operation(summary = "获取「支付」详情")
    @Parameter(name = "id", description = "Payment ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun get(@PathVariable("id") id: Long): Response<Payment> {
        val payment = paymentService.getPaymentById(id)
        return Response.success(payment)
    }

    @Operation(summary = "搜索「支付」")
    @RequestMapping("/search", method = [RequestMethod.POST])
    fun search(@RequestBody request: PaymentSearchRequest): Response<SearchResponse<Payment>> {
        val results = paymentService.searchPagingPayments(request)
        val count = paymentService.searchPagingPaymentsCount(request)
        val response = SearchResponse(results, count)
        return Response.success(response)
    }

    @Operation(summary = "预下单")
    @RequestMapping("/{id}/prepay", method = [RequestMethod.POST])
    fun prepay(@PathVariable("id") id: Long, servletRequest: HttpServletRequest): Response<WxPrepayResponse> {
        return paymentService.prepay(id, servletRequest)
    }

    @Operation(summary = "微信退款回调通知", hidden = true)
    @RequestMapping("/notify/wxpay/order", method = [RequestMethod.POST])
    @AllowAnonymous
    fun wxpayOrderNotify(@RequestBody xmlData: String): String {
        return paymentService.handleWxpayOrderNotify(xmlData)
    }

    @Operation(summary = "微信退款回调通知", hidden = true)
    @RequestMapping("/notify/wxpay/refund", method = [RequestMethod.POST])
    @AllowAnonymous
    fun wxpayRefundNotify(@RequestBody xmlData: String): String {
        return paymentService.handleWxpayRefundNotify(xmlData)
    }
}